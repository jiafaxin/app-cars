package com.autohome.app.cars.service.components.recrank.attention;

import com.autohome.app.cars.apiclient.rank.DsjClient;
import com.autohome.app.cars.apiclient.rank.dtos.SeriesHotRankDto;
import com.autohome.app.cars.common.BasePageModel;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiComponent;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKouBeiDto;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.attention.dtos.AreaSeriesAttentionDto;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.enums.RankLevelIdEnum;
import com.autohome.app.cars.service.services.dtos.PvItem;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/6/12
 */
@Slf4j
@Component
@DBConfig(tableName = "area_series_attention")
public class AreaSeriesAttentionComponent extends RankBaseComponent<List<AreaSeriesAttentionDto>> {

    @SuppressWarnings("all")
    @Autowired
    DsjClient dsjClient;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private RankCommonComponent rankCommonComponent;

    @Autowired
    private SeriesKouBeiComponent seriesKouBeiComponent;

    @Autowired
    private SeriesMapper seriesMapper;

    final static String areaIdParam = "areaId";

    TreeMap<String, Object> makeParam(int areaId) {
        return ParamBuilder.create(areaIdParam, areaId).build();
    }

    public CompletableFuture<List<AreaSeriesAttentionDto>> getAsync(int areaId) {
        return baseGetAsync(makeParam(areaId));
    }

    public List<AreaSeriesAttentionDto> get(int areaId) {
        return baseGet(makeParam(areaId));
    }

    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }

    public void refreshAll(Consumer<String> logInfo) {
        List<Integer> seriesIdList = seriesMapper.getAllSeriesIds();
        List<List<Integer>> seriesIdListList = Lists.partition(seriesIdList, 50);
        List<SeriesDetailDto> seriesDetailDtoList = new ArrayList<>();
        seriesIdListList.forEach(seriesIdListPart ->
                seriesDetailDtoList.addAll(seriesDetailComponent.getListSync(seriesIdListPart)));
        Map<Integer, SeriesDetailDto> seriesDetailMap = seriesDetailDtoList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesDetailDto::getId, e -> e, (k1, k2) -> k2));
        List<Integer> areaIds = new ArrayList<>(CityUtil.getAllProvinceIds());
        // 全国的id为-1
        areaIds.add(-1);
        areaIds.forEach(areaId -> {
            try {
                SeriesHotRankDto resultDto = dsjClient.getSeriesHotRankNew(areaId).join();
                if (Objects.nonNull(resultDto)
                        && Objects.nonNull(resultDto.getResult())
                        && !CollectionUtils.isEmpty((resultDto.getResult().getSeriesData()))
                        && Objects.nonNull(resultDto.getResult().getSeriesData().get(0))
                        && !CollectionUtils.isEmpty(resultDto.getResult().getSeriesData().get(0).getSeries())) {
                    List<AreaSeriesAttentionDto> attentionDtoList = new ArrayList<>();
                    List<AreaSeriesAttentionDto> finalAttentionDtoList = attentionDtoList;
                    resultDto.getResult().getSeriesData().get(0).getSeries().forEach(s -> {
                        if (Objects.nonNull(s) && Objects.nonNull(seriesDetailMap.get(s.getSeriesId()))) {
                            SeriesDetailDto seriesDetailDto = seriesDetailMap.get(s.getSeriesId());
                            AreaSeriesAttentionDto attentionDto = new AreaSeriesAttentionDto();
                            attentionDto.setSeriesId(s.getSeriesId());
                            attentionDto.setLevelId(seriesDetailDto.getLevelId());
                            attentionDto.setAtt(s.getAttNum());
                            attentionDto.setIsNewEnergy(seriesDetailDto.getEnergytype());
                            attentionDto.setFuelTypes(seriesDetailDto.getFueltypes());
                            attentionDto.setMinPrice(seriesDetailDto.getMinPrice());
                            attentionDto.setMaxPrice(seriesDetailDto.getMaxPrice());
                            attentionDto.setFctTypeId(seriesDetailDto.getPlace());
                            finalAttentionDtoList.add(attentionDto);
                        } else if (Objects.nonNull(s)) {
                            logInfo.accept(String.format("获取不到车系信息数据, areaId=%s, seriesId=%s", areaId, s.getSeriesId()));
                        } else {
                            logInfo.accept(String.format("接口返回值中有空值, areaId=%s", areaId));
                        }
                    });
                    // 在存储数据时就做好排序，为取数时节省时间
                    attentionDtoList = attentionDtoList.stream()
                            .sorted(Comparator.comparing(AreaSeriesAttentionDto::getAtt).reversed())
                            .toList();
                    if (!CollectionUtils.isEmpty(attentionDtoList)) {
                        update(makeParam(areaId), attentionDtoList);
                    }
                    logInfo.accept(String.format("数据刷新成功, areaId=%s", areaId));
                }
            } catch (Exception e) {
                logInfo.accept(String.format("数据刷新失败, areaId=%s, e=%s", areaId, e));
            }
        });
    }

    public RankResultDto getResultListByCondition(RankParam rankParam) {
        RankResultDto result = new RankResultDto();
        try {
            // 设置Result公共信息
            result.getResult().setSaleranktip(StrPool.EMPTY);
            RankLevelIdEnum rankLevelIdEnum = RankLevelIdEnum.getInstance(rankParam.getLevelid());
            if (Objects.nonNull(rankLevelIdEnum)) {
                result.getResult().setScenetitle(
                        rankCommonComponent.getPriceDesc(rankParam.getMinprice(), rankParam.getMaxprice())
                                + String.format(rankLevelIdEnum.getRankNameScheme(), "关注"));
            }
            result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");
            // 设置shareInfo
            result.getResult().setShareinfo(rankCommonComponent.getShareInfo());
            // 获取关注度信息
            int areaId = Math.max(rankParam.getProvinceid(), -1);
            List<AreaSeriesAttentionDto> dtoList = getByParams(areaId, rankParam.getLevelid(), rankParam.getFcttypeid(),
                    rankParam.getEnergytype(), rankParam.getMinprice(), rankParam.getMaxprice());
            // 分页处理
            dtoList = rankCommonComponent.pageHandle(result, rankParam, dtoList);
            if (!CollectionUtils.isEmpty(dtoList)) {
                // 获取车系id
                List<Integer> seriesIdList = dtoList.stream().map(AreaSeriesAttentionDto::getSeriesId).toList();
                // 获取相关信息
                AtomicReference<Map<Integer, SeriesDetailDto>> seriesDetailMap = new AtomicReference<>(new HashMap<>());
                AtomicReference<Map<Integer, SeriesKouBeiDto>> seriesKouBeiMap = new AtomicReference<>(new HashMap<>());
                seriesDetailComponent.getList(seriesIdList)
                        .thenCombine(seriesKouBeiComponent.getList(seriesIdList), (seriesDetail, seriesKouBei) -> {
                            seriesDetailMap.set(seriesDetail.stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity())));
                            seriesKouBeiMap.set(seriesKouBei.stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toMap(SeriesKouBeiDto::getSeriesId, Function.identity())));
                            return null;
                        }).join();

                for (int i = 0; i < dtoList.size(); i++) {
                    AreaSeriesAttentionDto item = dtoList.get(i);
                    RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
                    // 设置固定值
                    dto.setCardtype(1);
                    dto.setIsshowscorevalue(1);
                    dto.setScoretip("分");
                    dto.setSalecount(0L);
                    dto.setRightpricetitle("查成交价");
                    dto.setRcmtext(StrPool.EMPTY);
                    dto.setRcmdesc(StrPool.EMPTY);
                    dto.setSpecname(StrPool.EMPTY);
                    // 设置车系信息
                    SeriesDetailDto seriesDetail = seriesDetailMap.get().get(item.getSeriesId());
                    dto.setSeriesid(String.valueOf(item.getSeriesId()));
                    dto.setSeriesname(seriesDetail.getName());
                    dto.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%s&fromtype=107",
                            item.getSeriesId()));
                    dto.setPricelinkurl(RankUtil.genPriceLinkUrl(rankParam.getPm(), rankParam.getChannel(),
                            item.getSeriesId()));
                    dto.setShowenergyicon(item.getIsNewEnergy());
                    // 设置价格和车系图片
                    dto.setSeriesimage(ImageUtils.convertImageUrl(seriesDetail.getPngLogo(), true,
                            false, false,
                            ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                    dto.setPriceinfo(CommonHelper.priceForamtV2(seriesDetail.getMinPrice(),
                            seriesDetail.getMaxPrice()));

                    // 设置排名
                    String rankStr = rankCommonComponent.getRankStr(rankParam, i);
                    dto.setRank(rankStr);
                    // 关注分数
                    if (Objects.nonNull(seriesKouBeiMap.get().get(seriesDetail.getId()))
                            && Objects.nonNull(seriesKouBeiMap.get().get(seriesDetail.getId()).getScoreInfo())) {
                        dto.setScorevalue(CommonHelper.getRandInfo(String.valueOf(seriesKouBeiMap.get()
                                .get(seriesDetail.getId()).getScoreInfo().getAverage()), "0"));
                    }
                    // 设置PV
                    Map<String, String> pvArgs = new HashMap<>();
                    pvArgs.put("subranktypeid", "1");
                    pvArgs.put("rank", rankStr);
                    pvArgs.put("typeid", String.valueOf(rankParam.getTypeid()));
                    pvArgs.put("seriesid", String.valueOf(item.getSeriesId()));
                    dto.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_series_click", null,
                            "car_rec_main_rank_series_show", null));
                    // 设置RightInfo
                    dto.setRightinfo(genAttentionRightInfo(pvArgs, item.getAtt()));
                    result.getResult().getList().add(dto);
                }
            }
        } catch (Exception e) {
            log.error("关注度排行榜数据获取失败，rankParam:{}", rankParam, e);
        }

        // 返回结果统一业务逻辑处理
        rankCommonComponent.resultCommonDeal(result, rankParam);
        return result;
    }


    public List<AreaSeriesAttentionDto> getByParams(int areaId,
                                                    String levelIds, // 0全部
                                                    String fctTypeId, // 0/自主/合资/进口
                                                    int energyType, // 0不限,1燃油车,4纯电,5插电,6增程,456新能源
                                                    int minPrice,
                                                    int maxPrice) {
        List<AreaSeriesAttentionDto> attentionDtoList = get(areaId);
        if (CollectionUtils.isEmpty(attentionDtoList)) {
            return Collections.emptyList();
        }

        boolean filterByLevel = !List.of("0", "").contains((levelIds)); // 为0表示全部，是否按照级别进行过滤
        boolean filterEnergyType = energyType != 0; // 为0表示不限
        boolean filterFctTypeId = !List.of("0", "").contains(fctTypeId); // 为0表示不限
        List<String> energyTypeList = Arrays.asList(String.valueOf(energyType).split(""));

        return attentionDtoList.stream()
                .filter(e -> {
                    if (filterByLevel) {
                        return Arrays.asList(levelIds.split(",")).contains(String.valueOf(e.getLevelId()));
                    } else {
                        return true;
                    }
                })
                .filter(e -> {
                    if (filterFctTypeId) {
                        return e.getFctTypeId().equals(fctTypeId);
                    } else {
                        return true;
                    }
                })
                .filter(e -> {
                    if (filterEnergyType) {
                        if (energyType == 1) {
                            return e.getIsNewEnergy() == 0;
                        } else {
                            return CollectionUtils.containsAny(energyTypeList,
                                    Arrays.asList(e.getFuelTypes().split(",")));
                        }
                    } else {
                        return true;
                    }
                })
                .filter(e -> e.getMinPrice() <= maxPrice && e.getMaxPrice() >= minPrice)
                .toList();
    }

    private RankResultDto.RightinfoDTO genAttentionRightInfo(Map<String, String> argsMap, long attentionNum) {
        RankResultDto.RightinfoDTO rightInfo = new RankResultDto.RightinfoDTO();
        rightInfo.setPvitem(PvItem.getInstance(argsMap, "car_rec_main_rank_history_click", null,
                StrPool.EMPTY, null));
        rightInfo.setRighttextone(String.valueOf(attentionNum));
        rightInfo.setRighttexttwo("日均关注度");
        rightInfo.setPriceinfo(new RankResultDto.RightinfoDTO.PriceInfoDto());
        rightInfo.setExt(StrPool.EMPTY);
        rightInfo.setRightpriceeid(StrPool.EMPTY);
        rightInfo.setRightpricetitle(StrPool.EMPTY);
        rightInfo.setRightpriceurl(StrPool.EMPTY);
        rightInfo.setRighttexttwolinkurl(StrPool.EMPTY);
        return rightInfo;
    }

    /**
     *
     * @description 获取关注榜车系列表
     * @author zzli
     * @param pageIndex 第几页
     * @param pageSize 每页多少条
     * @param provinceId 省份id
     * @param filterSeriesId 过虑掉的车系
     * @param energyType 0不限,1燃油车,4纯电,5插电,6增程,456新能源
     * @param carType 0全部 1乘用车 2商用车
     */
    public BasePageModel<AreaSeriesAttentionDto> getSeriesDataList(int pageIndex,
                                           int pageSize,
                                           int provinceId,
                                           List<Integer> filterSeriesId,
                                           int energyType,
                                           int carType) {
        try {
            List<AreaSeriesAttentionDto> attentionDtoList = get(provinceId);
            if (CollectionUtils.isEmpty(attentionDtoList)) {
                attentionDtoList = get(-1);
            }
            // 车系过滤
            if (!CollectionUtils.isEmpty(filterSeriesId)) {
                attentionDtoList.removeIf(x -> filterSeriesId.contains(x.getSeriesId()));
            }
            //能源类型筛选
            List<AreaSeriesAttentionDto> areaSeriesAttentionDtos = attentionDtoList.stream()
                    .filter(e -> {
                        if (energyType != 0) {
                            if (energyType == 1) {
                                return e.getIsNewEnergy() == 0;
                            } else {
                                List<String> energyTypeList = Arrays.asList(String.valueOf(energyType).split(""));
                                return CollectionUtils.containsAny(energyTypeList,
                                        Arrays.asList(e.getFuelTypes().split(",")));
                            }
                        }
                        return true;
                    })
                    .filter(e -> {
                        if (carType>0) {
                            if (carType == 1) {
                                return !Level.isCVLevel(e.getLevelId());
                            } else if (carType == 2) {
                                return Level.isCVLevel(e.getLevelId());
                            }
                        }
                        return true;
                    }).toList();
            return new BasePageModel<>(pageIndex, pageSize, areaSeriesAttentionDtos);
        } catch (Exception e) {
            log.error("获取关注榜车系列表error", e);
            return new BasePageModel<>();
        }
    }
}
