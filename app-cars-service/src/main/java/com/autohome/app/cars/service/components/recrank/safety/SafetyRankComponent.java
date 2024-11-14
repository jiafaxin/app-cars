package com.autohome.app.cars.service.components.recrank.safety;

import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.mapper.popauto.CrashTestSeriesMapper;
import com.autohome.app.cars.mapper.popauto.entities.CrashCnCapSeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.CrashSeriesEntity;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiComponent;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKouBeiDto;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.enums.RankLevelIdEnum;
import com.autohome.app.cars.service.components.recrank.enums.SafetyLevelEnum;
import com.autohome.app.cars.service.components.recrank.safety.dto.SafetyRankDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * @date 2024/7/15
 */
@Slf4j
@Component
@DBConfig(tableName = "rank_safety")
public class SafetyRankComponent extends RankBaseComponent<List<SafetyRankDto>> {

    @Autowired
    private CrashTestSeriesMapper crashTestSeriesMapper;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private RankCommonComponent rankCommonComponent;

    @Autowired
    private SeriesKouBeiComponent seriesKouBeiComponent;

    final static String standardIdParam = "standardId";

    final static String orderTypeParam = "orderType";

    TreeMap<String, Object> makeParam(int standardId, int orderType) {
        return ParamBuilder.create(standardIdParam, standardId).add(orderTypeParam, orderType).build();
    }

    public CompletableFuture<List<SafetyRankDto>> getAsync(int standardId, int orderType) {
        return baseGetAsync(makeParam(standardId, orderType));
    }

    public List<SafetyRankDto> get(int standardId, int orderType) {
        return baseGet(makeParam(standardId, orderType));
    }

    public void refreshAll(Consumer<String> logInfo) {
        List<Integer> orderTypeList = List.of(1, 2, 3, 4);
        orderTypeList.forEach(orderType -> {
            try {
                List<CrashSeriesEntity> entityList =
                        crashTestSeriesMapper.getCrashTestData(orderType, 1);
                refreshCrashData(orderType, entityList);

                logInfo.accept(String.format("数据刷新成功, standardId=%s, orderType=%s", 1, orderType));
            } catch (Exception e) {
                logInfo.accept(String.format("数据刷新失败, standardId=%s, orderType=%s, e=%s", 1, orderType, e));
            }
        });

        try {
            List<CrashCnCapSeriesEntity> entityList =
                    crashTestSeriesMapper.getCrashCnCapTestData();
            refreshCnCapCrashData(entityList);

            logInfo.accept(String.format("数据刷新成功, standardId=%s, orderType=%s", 3, 0));
        } catch (Exception e) {
            logInfo.accept(String.format("数据刷新失败, standardId=%s, orderType=%s, e=%s", 3, 0, e));
        }
    }


    private void refreshCrashData(int orderType,
                                  List<CrashSeriesEntity> crashSeriesEntityList) {
        List<Integer> seriesIdList = crashSeriesEntityList.stream()
                .map(CrashSeriesEntity::getSeriesid)
                .distinct()
                .toList();
        Map<Integer, SeriesDetailDto> seriesDetailMap = seriesDetailComponent.getListSync(seriesIdList)
                .stream().filter(Objects::nonNull).collect(Collectors.toMap(SeriesDetailDto::getId, e -> e));
        Map<Integer, List<CrashSeriesEntity>> crashSeriesGroupBy = crashSeriesEntityList.stream()
                .collect(Collectors.groupingBy(CrashSeriesEntity::getSeriesid));
        List<CrashSeriesRankDto> dtoList = new ArrayList<>();
        crashSeriesGroupBy.forEach((key, value) -> {
            CrashSeriesRankDto crashSeriesRankDto = new CrashSeriesRankDto();
            crashSeriesRankDto.setSeriesId(key);
            crashSeriesRankDto.setItemList(value.stream()
                    .map(i -> new ItemDto(i.getItemid(), Integer.parseInt(i.getCrashvalue())))
                    .toList());
            dtoList.add(crashSeriesRankDto);
        });
        if (!CollectionUtils.isEmpty(dtoList)) {
            saveDtoList(1, orderType, dtoList, seriesDetailMap);
        }
    }

    private void refreshCnCapCrashData(List<CrashCnCapSeriesEntity> entityList) {
        List<Integer> seriesIdList = entityList.stream()
                .map(CrashCnCapSeriesEntity::getSeriesid)
                .distinct()
                .toList();
        Map<Integer, SeriesDetailDto> seriesDetailMap = seriesDetailComponent.getListSync(seriesIdList)
                .stream().filter(Objects::nonNull).collect(Collectors.toMap(SeriesDetailDto::getId, e -> e));
        List<CrashSeriesRankDto> dtoList = new ArrayList<>();
        entityList.forEach(entity -> {
            CrashSeriesRankDto crashSeriesRankDto = new CrashSeriesRankDto();
            crashSeriesRankDto.setSeriesId(entity.getId());
            crashSeriesRankDto.setCompScore(entity.getCompscore());
            crashSeriesRankDto.setStarScore(entity.getStarscore());
            crashSeriesRankDto.setItemList(Collections.emptyList());
            dtoList.add(crashSeriesRankDto);
        });
        if (!CollectionUtils.isEmpty(dtoList)) {
            saveDtoList(3, 0, dtoList, seriesDetailMap);
        }
    }

    private void saveDtoList(int standardId,
                             int orderType,
                             List<CrashSeriesRankDto> dtoList,
                             Map<Integer, SeriesDetailDto> seriesDetailMap) {
        List<SafetyRankDto> resultDtoList = new ArrayList<>();
        dtoList.forEach(dto -> {
            SeriesDetailDto seriesDetail = seriesDetailMap.get(dto.seriesId);
            if (Objects.nonNull(seriesDetail)) {
                SafetyRankDto safetyRankDto = new SafetyRankDto();
                safetyRankDto.setSeriesId(dto.getSeriesId());
                safetyRankDto.setCompScore(dto.getCompScore());
                safetyRankDto.setStarScore(dto.getStarScore());
                safetyRankDto.setItemList(dto.getItemList().stream()
                        .map(i -> new SafetyRankDto.Item(i.getItemId(), i.getTestValue()))
                        .toList());
                safetyRankDto.setLevelId(seriesDetail.getLevelId());
                safetyRankDto.setIsNewEnergy(seriesDetail.getEnergytype());
                safetyRankDto.setFuelTypes(seriesDetail.getFueltypes());
                safetyRankDto.setMinPrice(seriesDetail.getMinPrice());
                safetyRankDto.setMaxPrice(seriesDetail.getMaxPrice());
                resultDtoList.add(safetyRankDto);
            }
        });
        if (!CollectionUtils.isEmpty(resultDtoList)) {
            update(makeParam(standardId, orderType), resultDtoList);
        }
    }

    @Data
    private static class CrashSeriesRankDto {

        private int seriesId;
        private String compScore;
        private String starScore;
        private List<ItemDto> itemList;
    }

    @Data
    @AllArgsConstructor
    private static class ItemDto {
        private int itemId;
        private int testValue;
    }


    public RankResultDto getResultListByCondition(RankParam rankParam) {
        RankResultDto result = new RankResultDto();
        // 设置Result公共信息
        result.getResult().setSaleranktip(StringUtils.EMPTY);
        RankLevelIdEnum rankLevelIdEnum = RankLevelIdEnum.getInstance(rankParam.getLevelid());
        if (Objects.nonNull(rankLevelIdEnum)) {
            result.getResult().setScenetitle(rankCommonComponent.getPriceDesc(rankParam)
                    + String.format(rankLevelIdEnum.getRankNameScheme().replace("总", StringUtils.EMPTY), "安全"));
        }
        result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");
        // 设置shareInfo
        result.getResult().setShareinfo(rankCommonComponent.getShareInfo());

        int orderType = getIndexBySafetyTypeId(rankParam.getSafetypeid());
        try {
            // 查询数据
            List<SafetyRankDto> dtoList = getByParams(
                    rankParam.getDatatype() == 0
                            ? 1
                            : rankParam.getDatatype(),
                    orderType + 1,
                    rankParam.getLevelid(),
                    rankParam.getEnergytype(),
                    rankParam.getMinprice(),
                    rankParam.getMaxprice());
            // 分页处理
            dtoList = rankCommonComponent.pageHandle(result, rankParam, dtoList);
            if (!CollectionUtils.isEmpty(dtoList)) {
                List<Integer> seriesIdList = dtoList.stream()
                        .map(SafetyRankDto::getSeriesId)
                        .distinct()
                        .toList();
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

                for (int index = 0; index < dtoList.size(); index++) {
                    SafetyRankDto item = dtoList.get(index);
                    RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
                    dto.setCardtype(4);
                    String seriesIdStr = String.valueOf(item.getSeriesId());
                    SeriesDetailDto seriesDetail = seriesDetailMap.get().get(item.getSeriesId());
                    // 设置价格和车系图片
                    dto.setSeriesimage(seriesDetail.getPngLogo());
                    dto.setPriceinfo(CommonHelper.priceForamtV2(seriesDetail.getMinPrice(),
                            seriesDetail.getMaxPrice()));
              
                    dto.setSeriesid(seriesIdStr);
                    dto.setSeriesname(seriesDetail.getName());
                    dto.setShowenergyicon(seriesDetail.getEnergytype());
                    String rankStr = StringUtils.leftPad(String.valueOf(
                                    (rankParam.getPageindex() - 1) * rankParam.getPagesize() + index + 1),
                            2, "0");
                    dto.setRank(rankStr);
                    // 关注分数
                    if (Objects.nonNull(seriesKouBeiMap.get().get(seriesDetail.getId()))
                            && Objects.nonNull(seriesKouBeiMap.get().get(seriesDetail.getId()).getScoreInfo())) {
                        dto.setScorevalue(CommonHelper.getRandInfo(String.valueOf(seriesKouBeiMap.get()
                                .get(seriesDetail.getId()).getScoreInfo().getAverage()), "0"));
                    }
                    dto.setScoretip("分");
                    dto.setIsshowscorevalue(1);
                    // 只有中保研才设置
                    String safeLevelDesc = StringUtils.EMPTY;
                    if (1 == rankParam.getDatatype()) {
                        List<SafetyLevelEnum> levelEnums = genSafetyResultList(item.getItemList());
                        safeLevelDesc = levelEnums.get(orderType).getDesc();
                        List<String> safeResultList = levelEnums.stream()
                                .map(SafetyLevelEnum::getLevel)
                                .collect(Collectors.toList());
                        dto.setSaferesultlist(safeResultList);
                    }
                    // 设置PV
                    Map<String, String> pvArgs = new HashMap<>();
                    pvArgs.put("rank", rankStr);
                    pvArgs.put("typeid", String.valueOf(rankParam.getTypeid()));
                    pvArgs.put("seriesid", seriesIdStr);
                    dto.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%s&fromtype=107", seriesIdStr));
                    dto.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_series_click", null, "car_rec_main_rank_series_show", null));
                    dto.setRightinfo(genSafetyRankRightInfo(pvArgs, item.getCompScore(), seriesIdStr,
                            rankParam.getDatatype(), safeLevelDesc));
                    result.getResult().getList().add(dto);
                }
            }
        } catch (Exception e) {
            log.error("查询安全榜出错:{}", rankParam, e);
        }

        // 返回结果统一业务逻辑处理
        rankCommonComponent.resultCommonDeal(result, rankParam);
        return result;
    }

    public List<SafetyRankDto> getByParams(int standardId,
                                           int orderType,
                                           String levelIds, // 0全部
                                           int energyType, // 0不限,1燃油车,4纯电,5插电,6增程,456新能源
                                           int minPrice,
                                           int maxPrice) {
        List<SafetyRankDto> dataList = get(standardId, orderType);
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        boolean filterByLevel = !List.of("0", "").contains((levelIds)); // 为0表示全部，是否按照级别进行过滤
        boolean filterEnergyType = energyType != 0; // 为0表示不限
        List<String> energyTypeList = Arrays.asList(String.valueOf(energyType).split(""));

        return dataList.stream()
                .filter(e -> {
                    if (filterByLevel) {
                        return Arrays.asList(levelIds.split(",")).contains(String.valueOf(e.getLevelId()));
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

    private int getIndexBySafetyTypeId(int safetyTypeId) {
        if (safetyTypeId == 2) {
            return 0;
        } else if (safetyTypeId == 13) {
            return 1;
        } else if (safetyTypeId == 20) {
            return 2;
        } else if (safetyTypeId == 25) {
            return 3;
        }
        return 0;
    }

    private List<SafetyLevelEnum> genSafetyResultList(List<SafetyRankDto.Item> itemList) {
        List<SafetyLevelEnum> result = new ArrayList<>(itemList.size());
        if (!CollectionUtils.isEmpty(itemList)) {
            for (SafetyRankDto.Item item : itemList) {
                result.add(SafetyLevelEnum.getInstanceByKey(item.getTestValue()));
            }
        }
        return result;
    }

    /**
     * 生成安全榜rightInfo
     *
     * @param argsMap       参数
     * @param compScore     销量
     * @param seriesIdStr
     * @param dataType
     * @param safeLevelDesc
     * @return RankResultDto.RightinfoDTO
     */
    private RankResultDto.RightinfoDTO genSafetyRankRightInfo(Map<String, String> argsMap,
                                                              String compScore,
                                                              String seriesIdStr,
                                                              int dataType,
                                                              String safeLevelDesc) {
        RankResultDto.RightinfoDTO rightInfo = new RankResultDto.RightinfoDTO();

        String linkUrl = StringUtils.EMPTY;
        if (dataType == 1) {
            linkUrl = String.format("autohome://insidebrowserwk?url=%s",
                    UrlUtil.encode(String.format("https://m.autohome.com.cn/collisiontesting/%s/1?pvareaid=6841990",
                            seriesIdStr)));
        } else if (dataType == 3) {
            linkUrl = String.format("autohome://insidebrowserwk?loadtype=1&navalpha=0&url=%s",
                    UrlUtil.encode(String.format("https://zt.autohome.com.cn/landingpage/catarc-test-detail/index.html?seriesid=%s",
                            seriesIdStr)));
        }
        rightInfo.setRighttexttwolinkurl(linkUrl);
        rightInfo.setRighttextone(dataType == 1 ? safeLevelDesc : compScore.toString());
        rightInfo.setRighttexttwo("查看报告");
        PvItem pvItem = PvItem.getInstance(argsMap, "car_rec_main_rank_history_click", null,
                StringUtils.EMPTY, null);
        rightInfo.setPvitem(pvItem);
        return rightInfo;
    }

}
