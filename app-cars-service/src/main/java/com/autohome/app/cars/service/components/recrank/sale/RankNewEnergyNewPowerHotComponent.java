package com.autohome.app.cars.service.components.recrank.sale;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.NewPowerConfigDto;
import com.autohome.app.cars.service.components.recrank.dtos.NewPowerHotDataResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.uv.EsSeriesUvComponent;
import com.autohome.app.cars.service.components.uv.dto.EsSeriesUvItemDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 新能源榜-新势力热度榜组件
 */
@Component
@Slf4j
public class RankNewEnergyNewPowerHotComponent extends RankBaseComponent<NewPowerHotDataResultDto> {

    @Value("#{T(com.autohome.app.cars.service.components.recrank.dtos.NewPowerConfigDto).parseConfigDto('${newpower_rank_config:}')}")
    private NewPowerConfigDto newPowerRankConfig;


    @Resource
    private SeriesDetailComponent seriesDetailComponent;

    @Resource
    private RankCommonComponent rankCommonComponent;

    @Resource
    private EsSeriesUvComponent esSeriesUvComponent;

    private static final TreeMap<String, Object> makeParam = new TreeMap<>();
    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }


    public RankResultDto getResultListByCondition(RankParam param) {
        RankResultDto resultDto = new RankResultDto();
        // 查询缓存
        NewPowerHotDataResultDto dataResultDto = getFromRedis(makeParam);
        if (Objects.isNull(dataResultDto)) {
            // 从缓存中重新查询UV数据
            dataResultDto = getSourceDataFromRedis();
            if (Objects.nonNull(dataResultDto) && Objects.nonNull(dataResultDto.getDataList()) && !dataResultDto.getDataList().isEmpty()) {
                // 更新redis
                updateRedis(makeParam, JSONObject.toJSONString(dataResultDto), true);
            }
        }
        if (Objects.nonNull(dataResultDto) && Objects.nonNull(dataResultDto.getDataList()) && !dataResultDto.getDataList().isEmpty()) {
            dataResultDto.getDataList().forEach(data -> resultDto.getResult().getList().add(transToResult(data, param)));
        }
        if (!resultDto.getResult().getList().isEmpty()) {
            // 通过筛选条件过滤 过滤级别 + 在售状态
            filterByParam(resultDto, param);
            // 处理排名
            processRankNum(resultDto);
            
            rankCommonComponent.processOtherInfo(resultDto, param);
            // 分页
            //rankCommonComponent.pagination(resultDto, param);
            // 添加分享按钮
            //rankCommonComponent.addAskPriceBtn(resultDto, param);
        }
        resultDto.getResult().setMorescheme("");
        resultDto.getResult().setSaleranktip("");
        resultDto.getResult().setScenesubtitle("");
        resultDto.getResult().setScenetitle("新能源榜");
        return resultDto;
    }

    private void processRankNum(RankResultDto resultDto) {
        List<RankResultDto.ListDTO> list = resultDto.getResult().getList();
        for (int i = 0; i < list.size(); i++) {
            int rank = i + 1;
            list.get(i).setRn(rank);
            list.get(i).setRankNum(rank);
        }
    }

    private RankResultDto.ListDTO transToResult(NewPowerHotDataResultDto.RankDataDto item, RankParam param) {
        RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
        String seriesId = item.getSeriesId().toString();
        // 设置车系详细数据
        dto.setSeriesid(seriesId);
        // 获取车系信息
        dto.setSeriesimage(ImageUtils.convertImageUrl(item.getSeriesImage(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_No_Opt));
        dto.setSeriesname(item.getSeriesName());
        dto.setMinPrice(item.getMinPrice());
        dto.setMaxPrice(item.getMaxPrice());
        dto.setPriceinfo(CommonHelper.priceForamtV2(item.getMinPrice(), item.getMaxPrice()));
        dto.setLevelId(String.valueOf(item.getLevelId()));
        dto.setCardtype(1);
        // dto.setSpecname(StrPool.EMPTY);
        dto.setRcmdesc(StrPool.EMPTY);
        dto.setRcmtext(StrPool.EMPTY);
        // dto.setRighttextone();
        dto.setRighttexttwo(StrPool.EMPTY);
        dto.setRighttexttwolinkurl(StrPool.EMPTY);
        dto.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%s", seriesId));
        dto.setShowenergyicon(1);
        dto.setShowrankchange(0);
        dto.setRank(String.format("%02d", item.getRn()));
        dto.setRn(item.getRn());
        dto.setRankNum(item.getRn());
        dto.setState(item.getState());
        Map<String, String> pvArgs = new HashMap<>();
        pvArgs.put("subranktypeid", param.getSubranktypeid().toString());
        pvArgs.put("rank", dto.getRank());
        pvArgs.put("typeid", String.valueOf(param.getTypeid()));
        pvArgs.put("seriesid", seriesId);
        dto.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_series_click", null, "car_rec_main_rank_series_show", null));
        dto.setRightinfo(genRightInfo(pvArgs, item));
        return dto;
    }

    private RankResultDto.RightinfoDTO genRightInfo(Map<String, String> pvArgs, NewPowerHotDataResultDto.RankDataDto item) {
        RankResultDto.RightinfoDTO rightinfoDTO = new RankResultDto.RightinfoDTO();
        rightinfoDTO.setRighttextone(String.valueOf(item.getUvCount()));
        rightinfoDTO.setRightpriceeid(StrPool.EMPTY);
        rightinfoDTO.setRightpricetitle(StrPool.EMPTY);
        rightinfoDTO.setRightpriceurl(StrPool.EMPTY);
        rightinfoDTO.setPvitem(PvItem.getInstance(pvArgs, "", Collections.emptyMap(), "car_rec_main_rank_history_click", Collections.emptyMap()));
        return rightinfoDTO;
    }

    private void filterByParam(RankResultDto resultDto, RankParam param) {
        // 无数据不处理
        if (Objects.isNull(resultDto) || resultDto.getResult().getList().isEmpty()) {
            return;
        }
        List<String> levelIdList;
        if (StringUtils.hasLength(param.getLevelid()) && !param.getLevelid().equals("0")) {
            String[] levelIdArr = param.getLevelid().split(StrPool.COMMA);
            levelIdList = Arrays.asList(levelIdArr);
        } else {
            levelIdList = Collections.emptyList();
        }
        List<RankResultDto.ListDTO> list = resultDto.getResult().getList().stream()
                .filter(x -> (levelIdList.isEmpty() || levelIdList.contains(x.getLevelId()))
                        && (param.getIssale() == 0 || RankConstant.ON_SALE_STATE_LIST.contains(x.getState())))
                .toList();
        resultDto.getResult().setList(list);
    }


    /**
     * 从Redis中获取数据
     *
     * @return 新势力热度榜数据
     */
    public NewPowerHotDataResultDto getSourceDataFromRedis() {
        NewPowerHotDataResultDto resultDto = new NewPowerHotDataResultDto();
        // 去重
        newPowerRankConfig.setSeriesList(newPowerRankConfig.getSeriesList().stream().distinct().toList());
        List<EsSeriesUvItemDto> newPowerSeriesInfoList = new ArrayList<>(newPowerRankConfig.getSeriesList().size());
        Map<Integer, SeriesDetailDto> seriesDetailDtoMap = new HashMap<>(newPowerRankConfig.getSeriesList().size());
        CompletableFuture<Void> seriesDetailListFuture = seriesDetailComponent.getList(newPowerRankConfig.getSeriesList())
                .thenAccept(seriesDetailDtoList -> {
                    if (Objects.nonNull(seriesDetailDtoList) && !seriesDetailDtoList.isEmpty()) {
                        Map<Integer, SeriesDetailDto> detailDtoMap = seriesDetailDtoList.stream().filter(Objects::nonNull).collect(Collectors.toMap(SeriesDetailDto::getId, x -> x));
                        if (!detailDtoMap.isEmpty()) {
                            seriesDetailDtoMap.putAll(detailDtoMap);
                        }
                    }
                });
        // 从Redis中查询UV
        CompletableFuture<Void> uvDataFuture = CompletableFuture.runAsync(() -> {
            List<Integer> seriesIdList = newPowerRankConfig.getSeriesList().stream().distinct().toList();
            String dataStr = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            newPowerSeriesInfoList.addAll(seriesIdList.stream().map(seriesId -> esSeriesUvComponent.getSeriesDayUvByDateStr(dataStr, seriesId)).toList());
        });
        CompletableFuture.allOf(seriesDetailListFuture, uvDataFuture).join();
        // 组装数据
        if (!seriesDetailDtoMap.isEmpty() && !newPowerSeriesInfoList.isEmpty()) {
            List<NewPowerHotDataResultDto.RankDataDto> list = newPowerSeriesInfoList.stream()
                    .map(info -> {
                        if (Objects.nonNull(info) && seriesDetailDtoMap.containsKey(info.getSeriesId())) {
                            SeriesDetailDto seriesDetailDto = seriesDetailDtoMap.get(info.getSeriesId());
                            info.setState(seriesDetailDto.getState());
                            info.setLevelId(seriesDetailDto.getLevelId());
                            return NewPowerHotDataResultDto.RankDataDto.getInstance(info, seriesDetailDto);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingLong(NewPowerHotDataResultDto.RankDataDto::getUvCount).reversed()).toList();
            // 设置排名
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setRn(i + 1);
            }
            resultDto.setDataList(list);
        }
        return resultDto;
    }

    public void refreshCache(Consumer<String> xxlLog) {
        NewPowerHotDataResultDto dataResultDto = getSourceDataFromRedis();
        if (Objects.nonNull(dataResultDto) && Objects.nonNull(dataResultDto.getDataList()) && !dataResultDto.getDataList().isEmpty()) {
            // 更新redis
            updateRedis(makeParam, JSONObject.toJSONString(dataResultDto), true);
        }
        xxlLog.accept("更新新势力热度榜缓存成功");
    }
}
