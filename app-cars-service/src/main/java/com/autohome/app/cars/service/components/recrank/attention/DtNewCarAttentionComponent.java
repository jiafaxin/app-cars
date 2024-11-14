package com.autohome.app.cars.service.components.recrank.attention;

import com.autohome.app.cars.common.enums.ZiXunNewCarTagEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.appcars.AttRankNewCarMapper;
import com.autohome.app.cars.mapper.appcars.entities.AttRankNewCarEntity;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.attention.dtos.DtNewCarAttentionDto;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.enums.RankLevelIdEnum;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.autohome.app.cars.service.components.car.common.RankUtil.genPriceLinkUrl;

/**
 * @author chengjincheng
 * @date 2024/7/16
 */
@Slf4j
@Component
@DBConfig(tableName = "dt_new_car_attention")
public class DtNewCarAttentionComponent extends RankBaseComponent<List<DtNewCarAttentionDto>> {

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private RankCommonComponent rankCommonComponent;

    @Autowired
    private AttRankNewCarMapper attRankNewCarMapper;

    final static String dtParam = "dt";

    TreeMap<String, Object> makeParam(String dt) {
        return ParamBuilder.create(dtParam, dt).build();
    }

    public CompletableFuture<List<DtNewCarAttentionDto>> getAsync(String dt) {
        return baseGetAsync(makeParam(dt));
    }

    public List<DtNewCarAttentionDto> get(String dt) {
        return baseGet(makeParam(dt));
    }

    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }

    public void refreshAll(Consumer<String> logInfo) {
        // 查询db
        String dt = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        List<AttRankNewCarEntity> entityList = attRankNewCarMapper.getByDt(dt);
        if (CollectionUtils.isEmpty(entityList)) {
            if(LocalDateTime.now().getHour()>11){
                log.error("当日新车关注度数据未获取，dt={}", dt);
            }
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            dt = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
            entityList = attRankNewCarMapper.getByDt(dt);
        }

        // TODO chengjincheng 2024/10/14 昨天没数据，热修代码
        if (CollectionUtils.isEmpty(entityList)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            dt = DateFormatUtils.format(DateUtils.addDays(new Date(), -2), "yyyy-MM-dd");
            entityList = attRankNewCarMapper.getByDt(dt);
        }

        if (CollectionUtils.isEmpty(entityList)) {
            // 连续两天都查询不到新车关注度数据，属于异常情况，记录error日志
            logInfo.accept("获取不到新车关注度数据，dt=%s" + dt);
            return;
        }
        // 查询出来的车系id集合
        List<String> seriesIdStrList = entityList.stream()
                .map(AttRankNewCarEntity::getSeriesId)
                .map(String::valueOf)
                .collect(Collectors.toList());

        // 前一天的新车关注度信息，用于计算排名变化
        String yesterdayDt = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
        List<AttRankNewCarEntity> yesterdayEntityList =
                attRankNewCarMapper.getBySeriesIdsAndDt(String.join(",", seriesIdStrList), yesterdayDt);

        if(CollectionUtils.isEmpty(yesterdayEntityList)){
            String yesterdayDt2 = DateFormatUtils.format(DateUtils.addDays(new Date(), -2), "yyyy-MM-dd");
            yesterdayEntityList =
                    attRankNewCarMapper.getBySeriesIdsAndDt(String.join(",", seriesIdStrList), yesterdayDt2);
        }
        Map<Integer, AttRankNewCarEntity> yesterdayEntityMap = yesterdayEntityList.stream()
                .collect(Collectors.toMap(AttRankNewCarEntity::getSeriesId, Function.identity(), (v1, v2) -> v2));

        // 组装AttentionNewCarListDto
        List<DtNewCarAttentionDto> dtoList = new ArrayList<>();
        entityList.forEach(e -> {
            DtNewCarAttentionDto resultDto = new DtNewCarAttentionDto();
            resultDto.setSeriesId(e.getSeriesId());
            resultDto.setSeriesName(e.getSeriesName());
            resultDto.setLevelId(e.getLevelId());
            resultDto.setAtt(e.getAtt());
            resultDto.setOnTime(e.getOnTime());
            resultDto.setSeriesTagId(e.getSeriesTagId());
            resultDto.setSeriesTag(ZiXunNewCarTagEnum.getTypeByValue(e.getSeriesTagId()));
            resultDto.setArticleId(e.getArticleId());
            resultDto.setRankNum(e.getRankNum());
            // 排名变化
            AttRankNewCarEntity yesterdayEntity = yesterdayEntityMap.get(e.getSeriesId());
            resultDto.setRanChange(Objects.nonNull(yesterdayEntity)
                    ? yesterdayEntity.getRankNum() - e.getRankNum()
                    : 0);
            dtoList.add(resultDto);
        });

        if (!CollectionUtils.isEmpty(dtoList)) {
            update(makeParam(dt), dtoList);
        }
    }


    private List<DtNewCarAttentionDto> getByParams(RankParam rankParam) {
        String dt = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        List<DtNewCarAttentionDto> dtoList = get(dt);
        if (CollectionUtils.isEmpty(dtoList)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            dt = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
            dtoList = get(dt);
        }

        if (CollectionUtils.isEmpty(dtoList)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            dt = DateFormatUtils.format(DateUtils.addDays(new Date(), -2), "yyyy-MM-dd");
            dtoList = get(dt);
        }

        if (CollectionUtils.isEmpty(dtoList)) {
            // 连续两天都查询不到新车关注度数据，属于异常情况，记录error日志
            log.error("获取不到新车关注度数据，dt={}", dt);
            return Collections.emptyList();
        }

        String levelIds = rankParam.getLevelid();
        boolean filterByLevel = !List.of("0", "").contains((levelIds)); // 为0表示全部，是否按照级别进行过滤
        return dtoList.stream()
                .filter(e -> {
                    if (filterByLevel) {
                        return Arrays.asList(levelIds.split(",")).contains(String.valueOf(e.getLevelId()));
                    } else {
                        return true;
                    }
                })
                .toList();
    }


    /**
     * 获取默认新车关注榜列表
     * @return List<DtNewCarAttentionDto>
     */
    public List<DtNewCarAttentionDto> getRankList() {
        String dt = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        List<DtNewCarAttentionDto> dtoList = get(dt);
        if (CollectionUtils.isEmpty(dtoList)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            dt = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
            dtoList = get(dt);
        }

        if (CollectionUtils.isEmpty(dtoList)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            dt = DateFormatUtils.format(DateUtils.addDays(new Date(), -2), "yyyy-MM-dd");
            dtoList = get(dt);
        }

        if (CollectionUtils.isEmpty(dtoList)) {
            // 连续两天都查询不到新车关注度数据，属于异常情况，记录error日志
            log.error("获取不到新车关注度数据，dt={}", dt);
            return Collections.emptyList();
        }
        return dtoList;
    }

    public RankResultDto getResultListByCondition(RankParam param) {
        RankResultDto result = new RankResultDto();
        if (StringUtils.isBlank(param.getLevelid())) {
            param.setLevelid("0");
        }
        // 设置Result公共信息
        result.getResult().setSaleranktip(StringUtils.EMPTY);
        RankLevelIdEnum rankLevelIdEnum = RankLevelIdEnum.getInstance(param.getLevelid());
        if (Objects.nonNull(rankLevelIdEnum)) {
            result.getResult().setScenetitle(rankCommonComponent.getPriceDesc(param)
                    + String.format(rankLevelIdEnum.getRankNameScheme(), "关注"));
        }
        result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");
        // 设置shareInfo
        result.getResult().setShareinfo(rankCommonComponent.getShareInfo());

        try {
            // 获取数据
            List<DtNewCarAttentionDto> dtoList = getByParams(param);
            // 分页处理
            dtoList = rankCommonComponent.pageHandle(result, param, dtoList);

            if (!CollectionUtils.isEmpty(dtoList)) {
                List<Integer> seriesIdList = dtoList.stream().map(DtNewCarAttentionDto::getSeriesId)
                        .distinct().collect(Collectors.toList());
                Map<Integer, SeriesDetailDto> seriesDetailMap = seriesDetailComponent.getListSync(seriesIdList)
                        .stream().filter(Objects::nonNull)
                        .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity()));
                for (int i = 0; i < dtoList.size(); i++) {
                    DtNewCarAttentionDto item = dtoList.get(i);
                    RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
                    // 设置固定值
                    dto.setCardtype(1);
                    dto.setIsshowscorevalue(0);
                    dto.setShowrankchange(1);
                    // 设置车系信息
                    SeriesDetailDto seriesDetailDto = seriesDetailMap.get(item.getSeriesId());
                    if (Objects.isNull(seriesDetailDto)) {
                        continue;
                    }
                    dto.setSeriesid(String.valueOf(item.getSeriesId()));
                    dto.setSeriesname(seriesDetailDto.getName());
                    dto.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%s&fromtype=107",
                            item.getSeriesId()));
                    dto.setPricelinkurl(genPriceLinkUrl(param.getPm(), param.getChannel(), item.getSeriesId()));
                    dto.setShowenergyicon(seriesDetailDto.getEnergytype());
                    // 设置价格和车系图片
                    dto.setSeriesimage(ImageUtils.convertImageUrl(seriesDetailDto.getPngLogo(), true,
                            false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                    dto.setPriceinfo(CommonHelper.priceForamtV2(seriesDetailDto.getMinPrice(),
                            seriesDetailDto.getMaxPrice()));

                    // 设置排名
                    String rankStr = StringUtils.leftPad(String.valueOf(
                            (param.getPageindex() - 1) * param.getPagesize() + i + 1), 2, "0");
                    dto.setRank(rankStr);
                    // 排名变化
                    dto.setRankchange(item.getRanChange());
                    // 新车标签
                    dto.setSeriestag(item.getSeriesTag());
                    // 新车资讯
                    if (Objects.nonNull(item.getArticleId()) && item.getArticleId() > 0) {
                        String dateStr = getOnTimeDateStr(item);
                        dto.setRcmdesc(dateStr + "新车上市");
                        dto.setRcmtype(1);
                        dto.setRcmpic("http://nfiles3.autohome.com.cn/zrjcpk10/116100/main_rank_new_car_info.webp");
                        dto.setRcmlinkurl(String.format("autohome://article/articledetail?newsid=%s&newstype=0&articlefromtype=0&shieldpublish=0",
                                item.getArticleId()));
                        Map<String, String> rcmPvArgs = new HashMap<>();
                        rcmPvArgs.put("rank", rankStr);
                        rcmPvArgs.put("seriesid", String.valueOf(item.getSeriesId()));
                        dto.setRcmpvitem(PvItem.getInstance(rcmPvArgs, "new_car_info_click", null,
                                "new_car_info_show", null));
                    }
                    // 设置PV
                    Map<String, String> pvArgs = new HashMap<>();
                    pvArgs.put("subranktypeid", "1");
                    pvArgs.put("rank", rankStr);
                    pvArgs.put("typeid", String.valueOf(param.getTypeid()));
                    pvArgs.put("seriesid", String.valueOf(item.getSeriesId()));
                    dto.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_series_click", null,
                            "car_rec_main_rank_series_show", null));
                    // 设置RightInfo
                    dto.setRightinfo(genNewCarAttentionRightInfo(pvArgs, seriesDetailDto.getId(),
                            seriesDetailDto.getName(), item.getAtt()));
                    result.getResult().getList().add(dto);
                }
            } else {
                log.error("关注榜-新车关注榜无数据，注意检查。paramMap={}", JsonUtil.toString(param));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 返回结果统一业务逻辑处理
        rankCommonComponent.resultCommonDeal(result, param);
        return result;
    }

    private String getOnTimeDateStr(DtNewCarAttentionDto item) {
        if (StringUtils.isNotEmpty(item.getOnTime())) {
            try {
                return DateUtil.format(DateUtil.parse(item.getOnTime(), "yyyy-MM-dd"),
                        "MM月dd日");
            } catch (Exception e) {
                log.error("上市时间格式错误，item={}", item, e);
            }
        }
        return "";
    }

    private RankResultDto.RightinfoDTO genNewCarAttentionRightInfo(Map<String, String> argsMap,
                                                                   Integer seriesId,
                                                                   String seriesName,
                                                                   int att) {
        RankResultDto.RightinfoDTO rightInfo = new RankResultDto.RightinfoDTO();
        rightInfo.setPvitem(PvItem.getInstance(argsMap, "car_rec_main_rank_history_click",
                null, StringUtils.EMPTY, null));
        rightInfo.setRighttextone(String.valueOf(att));
        rightInfo.setRighttexttwo("关注度趋势");
        rightInfo.setRighttexttwolinkurl("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=" +
                UrlUtil.encode(String.format("rn://Car_SeriesSummary/AttentionHistory?panValid=0&seriesid=%s&seriesname=%s",
                        seriesId, UrlUtil.encode(seriesName).replace("+", "%20"))));
        return rightInfo;
    }


}
