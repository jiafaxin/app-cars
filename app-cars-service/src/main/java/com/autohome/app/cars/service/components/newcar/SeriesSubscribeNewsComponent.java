package com.autohome.app.cars.service.components.newcar;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.club.ClubApiClient;
import com.autohome.app.cars.apiclient.club.dtos.TopicContentResult;
import com.autohome.app.cars.apiclient.clubcard.ClubCardApiClient;
import com.autohome.app.cars.apiclient.clubcard.dtos.SeriesNewsResult;
import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.apiclient.cms.dtos.CmsArticleDataResult;
import com.autohome.app.cars.apiclient.subscribe.SubscribeClient;
import com.autohome.app.cars.apiclient.subscribe.dtos.SubscribedSeriesDto;
import com.autohome.app.cars.apiclient.user.UserApiClient;
import com.autohome.app.cars.apiclient.user.dtos.UserAuthSeriesResult;
import com.autohome.app.cars.apiclient.user.dtos.UserInfoResult;
import com.autohome.app.cars.apiclient.vr.PanoApiClient;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.enums.SeriesSubscribeNewsEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.appcars.SeriesSubscribeNewsMapper;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleMonthSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleWeekSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.SeriesSubscribeNewsEntity;
import com.autohome.app.cars.mapper.popauto.CarPhotoViewMapper;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SeriesTimeAxisMapper;
import com.autohome.app.cars.mapper.popauto.SpecParamMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarPhotoViewEntity;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecParamEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.hangqing.SpecCityPriceHisComponent;
import com.autohome.app.cars.service.components.hangqing.dtos.SpecCityPriceHisDto;
import com.autohome.app.cars.service.components.newcar.dtos.SeriesSubscribeNewsDto;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleMonthSourceComponent;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleWeekSourceComponent;
import com.autohome.app.cars.service.services.dtos.SubscribeConfig;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 动态频道组件 https://doc.autohome.com.cn/docapi/page/share/share_xYWiHyQGOW
 *
 * @author zhangchengtao
 * @date 2024/8/12 21:20
 */
@Component
@Slf4j
@DBConfig(tableName = "series_subscribe_news")
public class SeriesSubscribeNewsComponent extends BaseComponent<SeriesSubscribeNewsDto> {

    @Autowired
    private SeriesMapper seriesMapper;

    @Autowired
    private SeriesSubscribeNewsMapper seriesSubscribeNewsMapper;

    @Autowired
    private SpecCityPriceHisComponent specCityPriceHisComponent;

    @Autowired
    private RankSaleMonthSourceComponent rankSaleMonthSourceComponent;

    @Autowired
    private RankSaleWeekSourceComponent rankSaleWeekSourceComponent;
    @Autowired
    CarPhotoViewMapper carPhotoViewMapper;
    @Resource
    PanoApiClient panoApiClient;
    @Resource
    private ClubCardApiClient clubCardApiClient;
    @Autowired
    SeriesDetailComponent seriesDetailComponent;
    @Resource
    CmsApiClient cmsApiClient;
    @Resource
    ClubApiClient clubApiClient;
    @Resource
    private SubscribeClient subscribeClient;
    @Autowired
    SpecParamMapper specParamMapper;
    @Autowired
    SeriesTimeAxisMapper seriesTimeAxisMapper;
    @Resource
    UserApiClient userApiClient;

    @Autowired
    private SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;

    private final String seriesIdParamName = "series_id";
    private final String specIdParamName = "spec_id";
    private final String cityIdParamName = "city_id";
    private final String typeIdParamName = "biz_type";
    private final String isShowParamName = "is_show";
    private final String displayTimeParamName = "display_time";

    @Value("#{T(com.autohome.app.cars.service.services.dtos.SubscribeConfig).createFromJson('${series_subscribe_config:}')}")
    private SubscribeConfig seriesSubscribeConfig;

    /**
     * 销量趋势协议模板
     */
    private final static String RANK_HISTORY_SCHEME_TEMPLATE = "rn://Car_SeriesSummary/SaleHistory?panValid=0&seriesid=%d&seriesname=%s&typeid=%s&subranktypeid=%s&date=%s&energytype=%d";

    private static AtomicReference<LocalDateTime> beginDateTime = new AtomicReference<>(LocalDateTime.now().minusDays(180));
    private static final DateTimeFormatter publishTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");



    public TreeMap<String, Object> makeParam(int seriesId, int specId, int cityId, int isShow, int typeId, Timestamp displayTime) {
        return ParamBuilder.create(seriesIdParamName, seriesId)
                .add(typeIdParamName, typeId)
                .add(specIdParamName, specId)
                .add(cityIdParamName, cityId)
                .add(isShowParamName, isShow)
                .add(displayTimeParamName, displayTime)
                .build();
    }

    public TreeMap<String, Object> makeParam(int seriesId, int typeId) {
        return ParamBuilder.create(seriesIdParamName, seriesId)
                .add(typeIdParamName, typeId).build();
    }


    /**
     * Streaming 车系页入口使用
     * @param param
     * @return
     */
    public String getLatestUpdateNewsTime(TreeMap<String, Object> param) {
        String deviceId = (String) param.get("deviceId");
        Integer cityId = (Integer) param.get("cityId");
        CompletableFuture<String> completableFuture = subscribeClient.getSubscribeSeriesAndSpecList(deviceId).thenApply(resultDto -> {
            if (Objects.nonNull(resultDto) && Objects.nonNull(resultDto.getResult()) && !resultDto.getResult().isEmpty()) {
                List<SubscribedSeriesDto> list = resultDto.getResult();
                List<Integer> seriesIdList = list.stream().map(SubscribedSeriesDto::getSeriesId).toList();
                // 添加订阅车系标签
                return seriesDetailComponent.getList(seriesIdList).thenApply(x -> {
                    List<Integer> specIdList = list.stream().map(SubscribedSeriesDto::getSpecId).filter(specId -> specId > 0).toList();
                    List<SeriesSubscribeNewsEntity> subscribeSeriesList = seriesSubscribeNewsMapper.getSubscribeSeriesList(seriesIdList, specIdList, cityId);
                    if (Objects.nonNull(subscribeSeriesList) && !subscribeSeriesList.isEmpty()) {
                        return subscribeSeriesList.stream().map(SeriesSubscribeNewsEntity::getDisplay_time).max(Comparator.comparing(Timestamp::getTime)).get().getTime() + "";
                    }
                    return "0";
                }).exceptionally(e -> {
                    log.error("getLatestUpdateNewsTime error", e);
                    return "0";
                }).join();
            }
            return "0";
        }).exceptionally(e -> {
            log.error("getLatestUpdateNewsTime error", e);
            return "0";
        });
        return completableFuture.join();
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<SpecEntity> specList = seriesTimeAxisMapper.getSpecAll();
        List<Integer> cityIdList = CityUtil.getAllCityIds();
        Map<Integer, List<SpecEntity>> specMap = specList.stream().collect(Collectors.groupingBy(SpecEntity::getSeriesId));
        List<SeriesEntity> seriesList = seriesMapper.getAllSeries();
        seriesList.sort((s1, s2) -> Integer.compare(s2.getId(), s1.getId()));
        beginDateTime = new AtomicReference<>(LocalDateTime.now().minusDays(180));
        // 榜单数据
        String lastMonth = rankSaleMonthSourceComponent.getLastMonth();
        String lastWeek = rankSaleWeekSourceComponent.getLastWeek();
        Map<Integer, SeriesRankResultDto> rankMap = getRankMap(lastMonth, lastWeek);
        Date now = new Date();
        seriesList.stream().filter(x -> Arrays.asList(10, 20, 30).contains(x.getState())).forEach(seriesDto -> {
            try {
                int seriesId = seriesDto.getId();
                SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);
                //上市时间  ---   seriesDetailDto.getOnLineTime()

                List<SeriesSubscribeNewsEntity> lastNewsList = seriesSubscribeNewsMapper.selectLastAllType(seriesId);
                Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap = lastNewsList.stream().collect(Collectors.groupingBy(SeriesSubscribeNewsEntity::getBiz_type));
                // 刷新价格 (指导价&经销商价)
                List<SpecEntity> specEntityList = specMap.get(seriesId);
                refreshPriceDown(seriesDto, specEntityList, cityIdList, lastNewsGrpByBizTypeMap);
                // 刷新资讯动态
                refreshSeriesNews(seriesDto, lastNewsGrpByBizTypeMap, xxlLog);
                //刷新车图
                refreshSeriesPic(seriesDto, lastNewsGrpByBizTypeMap);
                //上市价格
                refreshSeriesPrice(seriesDto, lastNewsGrpByBizTypeMap, seriesDetailDto);
                //提车作业
                refreshSeriesFriendShare(seriesDto, lastNewsGrpByBizTypeMap, seriesDetailDto);
                //车系参配
                if (!CollectionUtils.isEmpty(specEntityList)) {
                    refreshSeriesParam(seriesDetailDto, lastNewsGrpByBizTypeMap,specEntityList);
                }
                // 刷新榜单 只有上市时间早于现在的车系才有销量数据
                if (rankMap.containsKey(seriesId) && Objects.nonNull(seriesDetailDto.getOnLineTime()) && seriesDetailDto.getOnLineTime().before(now)) {
                    refreshRankList(seriesDetailDto, lastNewsGrpByBizTypeMap, rankMap, xxlLog);
                }
            } catch (Exception e) {
                log.error("动态频道刷新数据错误", e);
                xxlLog.accept("动态频道刷新数据错误:"+seriesDto.getId());
            }
            xxlLog.accept(seriesDto.getId() + " success");
        });
    }


    // region 刷新价格
    private void refreshPriceDown(SeriesEntity seriesDto, List<SpecEntity> specList, List<Integer> cityIdList, Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap) {
        CompletableFuture<Void> dealerPriceFuture = CompletableFuture.runAsync(() -> refreshDealerPriceDown(seriesDto, specList, cityIdList, lastNewsGrpByBizTypeMap));
        CompletableFuture<Void> guidePriceFuture = CompletableFuture.runAsync(() -> refreshGuidePriceDown(seriesDto, specList, lastNewsGrpByBizTypeMap));
        CompletableFuture.allOf(dealerPriceFuture, guidePriceFuture).join();
    }

    private void refreshDealerPriceDown(SeriesEntity seriesDto, List<SpecEntity> specList, List<Integer> cityIdList, Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap) {
        int seriesId = seriesDto.getId();
        List<SeriesSubscribeNewsEntity> lastSpecDealerPriceList = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.SPEC_DEALER_PRICE.getType());
        List<SeriesSubscribeNewsEntity> lastSeriesDealerPriceList = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.SERIES_DEALER_PRICE.getType());

        // 车型-经销商价格 按 城市 分组
        Map<Integer, List<SeriesSubscribeNewsEntity>> specPriceGrpByCityMap;
        if (CollectionUtils.isEmpty(lastSpecDealerPriceList)) {
            specPriceGrpByCityMap = Collections.emptyMap();
        } else {
            specPriceGrpByCityMap = lastSpecDealerPriceList.stream().collect(Collectors.groupingBy(SeriesSubscribeNewsEntity::getCity_id));
        }

        Map<TreeMap<String, Object>, String> map = new HashMap<>();
        Map<Integer, SeriesSubscribeNewsEntity> citySeriesMap;
        if (CollectionUtils.isEmpty(lastSeriesDealerPriceList)) {
            citySeriesMap = Collections.emptyMap();
        } else {
            citySeriesMap = lastSeriesDealerPriceList.stream().collect(Collectors.toMap(SeriesSubscribeNewsEntity::getCity_id, x -> x));
        }
        Map<Integer, Map<Integer, List<SpecCityPriceHisDto>>> specCityPriceMap=new HashMap<>();
        if (!CollectionUtils.isEmpty(specList)) {
            List<Integer> specIdList = specList.stream().map(SpecEntity::getId).toList();
            specCityPriceMap = specCityPriceHisComponent.getSpecCityPriceMap(specIdList);
        }
        for (Integer cityId : cityIdList) {
            // 降价幅度最低的车型
            SeriesSubscribeNewsDto.SpecPriceDownDto minPriceDownSpec = null;
            // 降价幅度最高的车型
            SeriesSubscribeNewsDto.SpecPriceDownDto maxPriceDownSpec = null;
            int minSeriesPrice = 0;
            int maxSeriesPrice = 0;
            int maxPriceDownSpecId = 0;
            String maxPriceDownSpecName = StrPool.EMPTY;
            // 降价车型个数,  降价数量不依赖阈值, 只要降价就算
            int priceDownSpecCount = 0;
            int minDownPrice = Integer.MAX_VALUE;
            int maxDownPrice = Integer.MIN_VALUE;
            boolean specContainsCity = specPriceGrpByCityMap.containsKey(cityId);
            // 获取当前城市的所有车型经销商价格
            List<SeriesSubscribeNewsEntity> curCityAllSpecDealerPriceList = specContainsCity ? specPriceGrpByCityMap.get(cityId) : Collections.emptyList();
            // 当前城市车型经销商价格Map
            Map<Integer, SeriesSubscribeNewsEntity> curCitySpecPriceMap;
            if (specContainsCity && !curCityAllSpecDealerPriceList.isEmpty()) {
                curCitySpecPriceMap = curCityAllSpecDealerPriceList.stream().collect(Collectors.toMap(SeriesSubscribeNewsEntity::getSpec_id, x -> x));
            } else {
                curCitySpecPriceMap = Collections.emptyMap();
            }
            int minPriceGap = 0;
            int maxPriceGap = 0;
            int seriesIsShow = 0;
            if (!specCityPriceMap.containsKey(cityId)) {
                continue;
            }
            //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Map<Integer, List<SpecCityPriceHisDto>> cityMap = specCityPriceMap.get(cityId);
            LocalDateTime now = LocalDateTime.now();
            for (SpecEntity spec : specList) {
                int isShow = 0;
                int specId = spec.getId();
                // 是否存在历史价格
                boolean hasCurSpec = curCitySpecPriceMap.containsKey(specId);
                int pervSpecPrice = 0;
                if (hasCurSpec) {
                    String data = curCitySpecPriceMap.get(specId).getData();
                    SeriesSubscribeNewsDto.SpecPriceDownDto lastPriceDownDto = JsonUtil.toObject(data, SeriesSubscribeNewsDto.SpecPriceDownDto.class);
                    pervSpecPrice = lastPriceDownDto.getCurPrice();
                }
                // 当前车型没有价格更新, 跳过当前车型
                if (!cityMap.containsKey(specId)) {
                    continue;
                }
                List<SpecCityPriceHisDto> specCityPriceHisDtoList = cityMap.get(specId);

                // 当前车型经销商最新的历史价格
                SpecCityPriceHisDto specCityPriceHisDto = specCityPriceHisDtoList.get(specCityPriceHisDtoList.size() - 1);
                int priceGap = pervSpecPrice - specCityPriceHisDto.getNewsPrice();
                minSeriesPrice = Math.min(minSeriesPrice, specCityPriceHisDto.getNewsPrice());
                maxSeriesPrice = Math.max(maxSeriesPrice, specCityPriceHisDto.getNewsPrice());
                // 若存在历史价格且 价格未变动, 则跳过当前车型
                // 如果价格没有变动, 直接跳过
                if (hasCurSpec && priceGap == 0) {
                    continue;
                }
                if (pervSpecPrice != 0 && specCityPriceHisDto.getNewsPrice() == pervSpecPrice) {
                    continue;
                }
                SeriesSubscribeNewsDto.SpecPriceDownDto priceDownDto = new SeriesSubscribeNewsDto.SpecPriceDownDto();
                priceDownDto.setCurPrice(specCityPriceHisDto.getNewsPrice());
                priceDownDto.setPrevPrice(pervSpecPrice);
                priceDownDto.setSpecName(spec.getName());
                // 降价百分比
                double downPricePercent = (double) priceGap / pervSpecPrice;
                // 降价百分比大于阈值
                boolean biggerThanThreshold = downPricePercent > seriesSubscribeConfig.getPriceDownThreshold().getDealerThreshold();
                if (priceGap == 0) {
                    continue;
                }
                if (hasCurSpec && priceGap > 0) {
                    // 更新降价幅度最小车型
                    if (minPriceDownSpec == null || (biggerThanThreshold && priceGap < minPriceGap)) {
                        minPriceGap = priceGap;
                        minPriceDownSpec = priceDownDto;
                    }
                    // 更新降价幅度最大车型
                    if (maxPriceDownSpec == null || (biggerThanThreshold && priceGap > maxPriceGap)) {
                        maxPriceGap = priceGap;
                        maxPriceDownSpec = priceDownDto;
                        maxPriceDownSpecId = specId;
                        maxPriceDownSpecName = spec.getName();
                    }
                }
                // 只有车型已存在 且 价格降价时 更新降价车型数量 和 作为动态展示
                if (hasCurSpec && priceGap > 0) {
                    // 更新降价车型数量
                    priceDownSpecCount++;
                    if (biggerThanThreshold) {
                        isShow = 1;
                        seriesIsShow = 1;
                        minDownPrice = Math.min(minDownPrice, priceGap);
                        maxDownPrice = Math.max(maxDownPrice, priceGap);
                    }
                }
                // 当前城市车型经销商价格对象
                now = now.plusSeconds(1);
                map.put(makeParam(seriesId, specId, cityId, isShow, SeriesSubscribeNewsEnum.SPEC_DEALER_PRICE.getType(), Timestamp.valueOf(now)), JSONObject.toJSONString(priceDownDto));

            }
            int minPervPrice = 0;
            int maxPervPrice = 0;
            SeriesSubscribeNewsEntity lastSeriesPriceDto;
            if (citySeriesMap.containsKey(cityId)) {
                lastSeriesPriceDto = citySeriesMap.get(cityId);
                SeriesSubscribeNewsDto.SeriesPriceDownDto seriesPriceDownDataDto = JSONObject.parseObject(lastSeriesPriceDto.getData(), SeriesSubscribeNewsDto.SeriesPriceDownDto.class);
                minPervPrice = seriesPriceDownDataDto.getCurMinPrice();
                maxPervPrice = seriesPriceDownDataDto.getCurMaxPrice();
            }
            // 车系最低价或最高价有变动时才更新
            CompletableFuture<SeriesCityAskPriceDto> seriesCityAskPriceFuture = seriesCityAskPriceNewComponent.get(seriesId, cityId);
            SeriesCityAskPriceDto seriesDealerPrice = seriesCityAskPriceFuture.join();
            if (Objects.nonNull(seriesDealerPrice)) {
                minSeriesPrice = seriesDealerPrice.getMinPrice();
                maxSeriesPrice = seriesDealerPrice.getMaxPrice();
            }
            if (minSeriesPrice != minPervPrice || maxSeriesPrice != maxPervPrice) {
                // 当前车系经销商价格
                SeriesSubscribeNewsDto.SeriesPriceDownDto seriesPriceDownDto = new SeriesSubscribeNewsDto.SeriesPriceDownDto();
                seriesPriceDownDto.setCurMinPrice(minSeriesPrice);
                seriesPriceDownDto.setCurMaxPrice(maxSeriesPrice);
                seriesPriceDownDto.setPrevMinPrice(minPervPrice);
                seriesPriceDownDto.setPrevMaxPrice(maxPervPrice);
                seriesPriceDownDto.setMinPriceDown(minDownPrice);
                seriesPriceDownDto.setMaxPriceDown(maxDownPrice);
                seriesPriceDownDto.setCount(priceDownSpecCount);
                seriesPriceDownDto.setSpecId(maxPriceDownSpecId);
                if (Objects.nonNull(maxPriceDownSpec)) {
                    seriesPriceDownDto.setSpecName(maxPriceDownSpecName);
                } else {
                    seriesPriceDownDto.setSpecName(StrPool.EMPTY);
                }
                now = now.plusSeconds(1);
                map.put(makeParam(seriesId, 0, cityId, seriesIsShow, SeriesSubscribeNewsEnum.SERIES_DEALER_PRICE.getType(), Timestamp.valueOf(now)), JSONObject.toJSONString(seriesPriceDownDto));
            }
        }

        if (!map.isEmpty()) {
            updateDBBatch(map);
        }
    }


    /**
     * 更新指导价
     *
     * @param seriesDto               车系dto
     * @param specList                车型Dto List
     * @param lastNewsGrpByBizTypeMap 动态历史Map(按类型分组)
     */
    private void refreshGuidePriceDown(SeriesEntity seriesDto, List<SpecEntity> specList, Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap) {
        if (specList.isEmpty()) {
            return;
        }
        int seriesId = seriesDto.getId();
        List<SeriesSubscribeNewsEntity> lastSeriesGuidePriceList = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.SERIES_GUIDE_PRICE.getType());
        List<SeriesSubscribeNewsEntity> lastSpecGuidePriceList = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.SPEC_GUIDE_PRICE.getType());
        // 降价车型个数
        int priceDownSpecCount = 0;
        // 最大降幅车型ID
        int maxPriceDownSpecId = 0;
        // 最大降幅车型名
        String maxPriceDownSpecName = StrPool.EMPTY;
        // 最小降价差额
        int minPriceGap = 0;
        // 最大降价差额
        int maxPriceGap = 0;
        double maxPriceDrop = 0;
        int seriesIsShow = 0;
        Map<TreeMap<String, Object>, String> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        int minDownPrice = Integer.MAX_VALUE;
        int maxDownPrice = Integer.MIN_VALUE;
        // 新增车型
        if (Objects.isNull(lastSpecGuidePriceList) || lastSpecGuidePriceList.isEmpty()) {
            for (SpecEntity specEntity : specList) {
                SeriesSubscribeNewsDto.SpecPriceDownDto specPriceDownDto = new SeriesSubscribeNewsDto.SpecPriceDownDto();
                specPriceDownDto.setCurPrice(specEntity.getMinPrice());
                specPriceDownDto.setSpecName(specEntity.getName());
                specPriceDownDto.setPrevPrice(0);
                now = now.plusSeconds(1);
                map.put(makeParam(seriesId, specEntity.getId(), 0, 0, SeriesSubscribeNewsEnum.SPEC_GUIDE_PRICE.getType(), Timestamp.valueOf(now)), JSONObject.toJSONString(specPriceDownDto));
            }
        } else {
            Map<Integer, SeriesSubscribeNewsEntity> lastSpecMap = lastSpecGuidePriceList.stream().collect(Collectors.toMap(SeriesSubscribeNewsEntity::getSpec_id, x -> x));
            for (SpecEntity specEntity : specList) {
                int isShow = 0;
                int specId = specEntity.getId();
                boolean hasSpec = lastSpecMap.containsKey(specEntity.getId());
                SeriesSubscribeNewsDto.SpecPriceDownDto lastSpecPriceDownDto;
                int lastSpecPrice = 0;
                if (hasSpec) {
                    lastSpecPriceDownDto = JSONObject.parseObject(lastSpecMap.get(specId).getData(), SeriesSubscribeNewsDto.SpecPriceDownDto.class);
                    lastSpecPrice = lastSpecPriceDownDto.getCurPrice();
                    int priceGap = lastSpecPrice - specEntity.getMinPrice();
                    double priceDrop = (double) priceGap / lastSpecPrice;
                    if (priceGap == 0) {
                        continue;
                    }

                    boolean biggerThanThreshold = priceGap > seriesSubscribeConfig.getPriceDownThreshold().getGuideThreshold();
                    if (biggerThanThreshold) {
                        minDownPrice = Math.min(minDownPrice, priceGap);
                        maxDownPrice = Math.max(maxDownPrice, priceGap);
                        isShow = 1;
                        seriesIsShow = 1;
                        priceDownSpecCount++;
                        if (priceGap < minPriceGap) {
                            minPriceGap = priceGap;
                        }
                        if (priceDrop > maxPriceDrop) {
                            maxPriceDrop = priceDrop;
                            maxPriceDownSpecName = specEntity.getName();
                            maxPriceDownSpecId = specId;
                        }
                    }
                }
                SeriesSubscribeNewsDto.SpecPriceDownDto specPriceDownDto = new SeriesSubscribeNewsDto.SpecPriceDownDto();
                specPriceDownDto.setCurPrice(specEntity.getMinPrice());
                specPriceDownDto.setSpecName(specEntity.getName());
                specPriceDownDto.setPrevPrice(lastSpecPrice);
                now = now.plusSeconds(1);
                map.put(makeParam(seriesId, specId, 0, isShow, SeriesSubscribeNewsEnum.SPEC_GUIDE_PRICE.getType(), Timestamp.valueOf(now)), JSONObject.toJSONString(specPriceDownDto));
            }

        }


        // 新增车系
        now = now.plusNanos(1000);
        if (Objects.isNull(lastSeriesGuidePriceList) || lastSeriesGuidePriceList.isEmpty()) {
            SeriesSubscribeNewsDto.SeriesPriceDownDto seriesPriceDownDto = new SeriesSubscribeNewsDto.SeriesPriceDownDto();
            seriesPriceDownDto.setCurMinPrice(seriesDto.getSeriesPriceMin());
            seriesPriceDownDto.setCurMaxPrice(seriesDto.getSeriesPriceMax());
            seriesPriceDownDto.setMaxPriceDown(0);
            seriesPriceDownDto.setMinPriceDown(0);
            seriesPriceDownDto.setSpecName(StrPool.EMPTY);
            seriesPriceDownDto.setPrevMinPrice(0);
            seriesPriceDownDto.setPrevMaxPrice(0);
            seriesPriceDownDto.setCount(0);
            map.put(makeParam(seriesId, 0, 0, 0, SeriesSubscribeNewsEnum.SERIES_GUIDE_PRICE.getType(), Timestamp.valueOf(now)), JSONObject.toJSONString(seriesPriceDownDto));
        } else {
            String data = lastSeriesGuidePriceList.get(0).getData();
            SeriesSubscribeNewsDto.SeriesPriceDownDto lastSeriesPriceDownDto = JSONObject.parseObject(data, SeriesSubscribeNewsDto.SeriesPriceDownDto.class);
            // 如果价格有变动
            if (seriesDto.getSeriesPriceMin() != lastSeriesPriceDownDto.getCurMinPrice() || seriesDto.getSeriesPriceMax() != lastSeriesPriceDownDto.getCurMaxPrice()) {
                SeriesSubscribeNewsDto.SeriesPriceDownDto seriesPriceDownDto = new SeriesSubscribeNewsDto.SeriesPriceDownDto();
                seriesPriceDownDto.setCurMinPrice(seriesDto.getSeriesPriceMin());
                seriesPriceDownDto.setCurMaxPrice(seriesDto.getSeriesPriceMax());
                seriesPriceDownDto.setMinPriceDown(minDownPrice);
                seriesPriceDownDto.setMaxPriceDown(maxDownPrice);
                seriesPriceDownDto.setSpecName(maxPriceDownSpecName);
                seriesPriceDownDto.setSpecId(maxPriceDownSpecId);
                seriesPriceDownDto.setPrevMinPrice(lastSeriesPriceDownDto.getCurMinPrice());
                seriesPriceDownDto.setPrevMaxPrice(lastSeriesPriceDownDto.getCurMaxPrice());
                seriesPriceDownDto.setCount(priceDownSpecCount);
                map.put(makeParam(seriesId, 0, 0, seriesIsShow, SeriesSubscribeNewsEnum.SERIES_GUIDE_PRICE.getType(), Timestamp.valueOf(now)), JSONObject.toJSONString(seriesPriceDownDto));
            }
        }
        if (!map.isEmpty()) {
            updateDBBatch(map);
        }
    }

    // endregion

    /**
     * CMS 图文内容包含的标签
     */
    private final List<Integer> cmsTagIdList = Arrays.asList(50, 54, 67, 68, 69, 72, 73, 74, 123, 2835, 10398, 10400, 10402, 10404, 10406, 11078, 11079, 16535, 26309);
    /**
     * 视频内容作者ID列表
     */
    private final List<Integer> videoAuthorIdList = Arrays.asList(19987472, 79195414, 247392112, 260971446, 260971611);


    private void refreshSeriesNews(SeriesEntity seriesDto, Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap, Consumer<String> xxlLog) {
        Map<TreeMap<String, Object>, String> map = new HashMap<>();
        CompletableFuture<BaseModel<SeriesNewsResult>> seriesNewsFuture = clubCardApiClient.getSeriesNews(seriesDto.getId(), 1, 100, 1);
        List<SeriesSubscribeNewsEntity> lastCmsNewsList = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.CMS_NEWS.getType());
        List<SeriesSubscribeNewsEntity> lastVideoNewsList = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.VIDEO_NEWS.getType());
        LocalDateTime maxPublishTime = getMaxPublishTime(lastCmsNewsList, lastVideoNewsList);
        beginDateTime.set(maxPublishTime.isAfter(beginDateTime.get()) ? maxPublishTime : beginDateTime.get());
        seriesNewsFuture.thenAccept(seriesNewsResult -> {
            SeriesNewsResult result = seriesNewsResult.getResult();
            if (result != null && !CollectionUtils.isEmpty(result.getItems())) {
                List<SeriesNewsResult.ItemsDTO> itemList = result.getItems().stream().filter(this::isThisNewsEligible).toList();
                if (!CollectionUtils.isEmpty(itemList)) {
                    for (SeriesNewsResult.ItemsDTO itemsDTO : itemList) {
                        SeriesSubscribeNewsDto.CmsNewsDto cmsNewsDto = getCmsNewsDto(itemsDTO);
                        switch (itemsDTO.getMain_data_type()) {
                            case "cms":
                                if (StringUtils.isNotEmpty(itemsDTO.getMulti_images())) {
                                    cmsNewsDto.setImgUrlList(Arrays.asList(itemsDTO.getMulti_images().split(StrPool.COMMA)));
                                }else {
                                    cmsNewsDto.setImgUrlList(Collections.singletonList(itemsDTO.getImg_url()));
                                }
                                cmsNewsDto.setScheme(String.format("autohome://article/articledetailcolumn?newsid=%d&newstype=0&mediatype=0", itemsDTO.getBiz_id()));
                                map.put(makeParam(seriesDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.CMS_NEWS.getType(), Timestamp.valueOf(cmsNewsDto.getPublishTime())), JSONObject.toJSONString(cmsNewsDto));
                                break;
                            case "video":
                                cmsNewsDto.setImgUrlList(Collections.singletonList(ImageUtils.convertImageUrl(itemsDTO.getImg_url(), true, false, false, ImageSizeEnum.ImgSize_16x9_684x0, false, false, false)));
                                cmsNewsDto.setScheme(String.format("autohome://article/slidevideodetail?newsid=%d&mediatype=0", itemsDTO.getBiz_id()));
                                map.put(makeParam(seriesDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.VIDEO_NEWS.getType(), Timestamp.valueOf(cmsNewsDto.getPublishTime())), JSONObject.toJSONString(cmsNewsDto));
                                break;
                        }
                    }
                }
            }
        }).exceptionally(e -> {
            log.warn("查询资讯接口出错");
            return null;
        }).join();
        if (!map.isEmpty()) {
            updateDBBatch(map);
        }
    }


    private static SeriesSubscribeNewsDto.CmsNewsDto getCmsNewsDto(SeriesNewsResult.ItemsDTO itemsDTO) {
        SeriesSubscribeNewsDto.CmsNewsDto cmsNewsDto = new SeriesSubscribeNewsDto.CmsNewsDto();
        cmsNewsDto.setAuthorId(itemsDTO.getAuthor_id());
        cmsNewsDto.setAuthorImg(itemsDTO.getAuthor_img());
        cmsNewsDto.setAuthorName(itemsDTO.getAuthor_name());
        cmsNewsDto.setBizId(itemsDTO.getBiz_id());
        cmsNewsDto.setGlobalId(itemsDTO.getGlobal_id());
        cmsNewsDto.setTitle(itemsDTO.getTitle());
        cmsNewsDto.setMainDataType(itemsDTO.getMain_data_type());
        cmsNewsDto.setPublishTime(LocalDateTime.parse(itemsDTO.getPublish_time(), publishTimeFormatter));
        return cmsNewsDto;
    }

    /**
     * 校验当前资讯是否符合条件
     *
     * @param news 资讯对象
     * @return 是否符合条件
     */
    private boolean isThisNewsEligible(SeriesNewsResult.ItemsDTO news) {
        if (Objects.isNull(news)) {
            return false;
        }
        // 过滤只关联当前车关系的动态
        if (CollectionUtils.isEmpty(news.getSeries_ids()) || news.getSeries_ids().size() != 1) {
            return false;
        }

        LocalDateTime publishTime = LocalDateTime.parse(news.getPublish_time(), publishTimeFormatter);
        // 判断是否符合 发布时间是180天内
        if (!publishTime.isAfter(beginDateTime.get())) {
            return false;
        }

        return switch (news.getMain_data_type()) {
            // 图文内容按标签取
            case "cms" ->
                    !CollectionUtils.isEmpty(news.getCms_tags_ids()) && news.getCms_tags_ids().stream().anyMatch(cmsTagIdList::contains);
            // 视频 取作者列表下的 横向视频
            case "video" -> !news.getTitle().contains("预告") && videoAuthorIdList.contains(news.getAuthor_id()) && news.getVideo_direction() == 1;
            default -> false;
        };
    }

    private LocalDateTime getMaxPublishTime(List<SeriesSubscribeNewsEntity> cmsList, List<SeriesSubscribeNewsEntity> videoList) {
        LocalDateTime localDateTime = LocalDateTime.MIN;
        if (!CollectionUtils.isEmpty(cmsList)) {
            SeriesSubscribeNewsDto.CmsNewsDto cmsNewsDto;
            if (cmsList.size() == 1) {
                cmsNewsDto = JSONObject.parseObject(cmsList.get(0).getData(), SeriesSubscribeNewsDto.CmsNewsDto.class);
            } else {
                cmsNewsDto = cmsList.stream().map(x -> JSONObject.parseObject(x.getData(), SeriesSubscribeNewsDto.CmsNewsDto.class)).max(Comparator.comparing(SeriesSubscribeNewsDto.CmsNewsDto::getPublishTime)).get();
            }
            if (localDateTime.isBefore(cmsNewsDto.getPublishTime())) {
                localDateTime = cmsNewsDto.getPublishTime();
            }
        }
        if (!CollectionUtils.isEmpty(videoList)) {
            SeriesSubscribeNewsDto.CmsNewsDto videoNewsDto;
            if (videoList.size() == 1) {
                videoNewsDto = JSONObject.parseObject(videoList.get(0).getData(), SeriesSubscribeNewsDto.CmsNewsDto.class);
            } else {
                videoNewsDto = videoList.stream().map(x -> JSONObject.parseObject(x.getData(), SeriesSubscribeNewsDto.CmsNewsDto.class)).max(Comparator.comparing(SeriesSubscribeNewsDto.CmsNewsDto::getPublishTime)).get();
            }
            if (localDateTime.isBefore(videoNewsDto.getPublishTime())) {
                localDateTime = videoNewsDto.getPublishTime();
            }
        }
        return localDateTime;
    }

    /**
     * 图片更新
     */
    void refreshSeriesPic(SeriesEntity seriesDto, Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap) {

        List<SeriesSubscribeNewsEntity> lastPic = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.IMAGE.getType());
        String publishTime="";
        SeriesSubscribeNewsDto.PicDTO lastPicDto = null;
        if (!CollectionUtils.isEmpty(lastPic)) {
            if (lastPic.size() == 1) {
                lastPicDto = JSONObject.parseObject(lastPic.get(0).getData(), SeriesSubscribeNewsDto.PicDTO.class);
            } else {
                lastPicDto = lastPic.stream().map(x -> JSONObject.parseObject(x.getData(), SeriesSubscribeNewsDto.PicDTO.class)).max(Comparator.comparing(SeriesSubscribeNewsDto.PicDTO::getPublishTime)).get();
            }
            publishTime=DateUtil.format(lastPicDto.getPublishTime(),"yyyy-MM-dd HH:mm:ss");
        }


        List<CarPhotoViewEntity> carPhotoViewEntityList = carPhotoViewMapper.getPhotoViewBySeriesId(seriesDto.getId(),publishTime);
        if (CollectionUtils.isEmpty(carPhotoViewEntityList)) {
            return;
        }

        SeriesSubscribeNewsDto.PicDTO picDTO = new SeriesSubscribeNewsDto.PicDTO();

        carPhotoViewEntityList = carPhotoViewEntityList.stream().sorted(Comparator.comparing(CarPhotoViewEntity::getClassOrder)
                .thenComparing(CarPhotoViewEntity::getShowId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewEntity::getSourceTypeOrder)
                .thenComparing(CarPhotoViewEntity::getDealerPicOrder)
                .thenComparing(CarPhotoViewEntity::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewEntity::getPicId, Comparator.reverseOrder())
        ).collect(Collectors.toList());

        picDTO.setPublishTime(carPhotoViewEntityList.stream().max(Comparator.comparing(CarPhotoViewEntity::getPicUploadTime)).get().getPicUploadTime());

        picDTO.setPicCount(carPhotoViewEntityList.size());
        // 校验已经存在
        if (Objects.nonNull(lastPicDto) && !picDTO.getPublishTime().after(lastPicDto.getPublishTime())) {
            return;
        }
        picDTO.setLinkurl(String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s", seriesDto.getId(), UrlUtil.encode(seriesDto.getName())));
        //外观下是否有颜色,且颜色数量>1
        Map<Integer, List<CarPhotoViewEntity>> outPicMap = carPhotoViewEntityList.stream()
                .filter(x -> x.getPicClass() == 1 && x.getPicColorId() != 0)
                .collect(Collectors.groupingBy(CarPhotoViewEntity::getPicColorId));
        if (!outPicMap.isEmpty()) {
            BaseModel<SeriesVrExteriorResult> vr = panoApiClient.getSeriesExterior(seriesDto.getId()).join();
            for (Map.Entry<Integer, List<CarPhotoViewEntity>> carPhotoViewMap : outPicMap.entrySet()) {
                //如果只有一种，将一种颜色的外观图放出四张
                if (outPicMap.size() == 1) {
                    carPhotoViewMap.getValue().stream().limit(4).forEach(x -> {
                        SeriesSubscribeNewsDto.PicDTO.picItemsDTO item = new SeriesSubscribeNewsDto.PicDTO.picItemsDTO();
                        item.setPic(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(x.getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                        item.setUrl(String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=%s", seriesDto.getId(), UrlUtil.encode(seriesDto.getName()), x.getPicClass()));
                        picDTO.getPicItems().add(item);
                    });
                } else {
                    SeriesSubscribeNewsDto.PicDTO.picItemsDTO item = new SeriesSubscribeNewsDto.PicDTO.picItemsDTO();
                    item.setColorName(carPhotoViewMap.getValue().get(0).getColorname());
                    item.setPic(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(carPhotoViewMap.getValue().get(0).getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                    String url = String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=1&colorid=%s", seriesDto.getId(), UrlUtil.encode(seriesDto.getName()), carPhotoViewMap.getValue().get(0).getPicColorId());
                    //vr外观颜色
                    if (vr != null && vr.getResult() != null && vr.getResult().getColor_list() != null && vr.getResult().getColor_list().size() > 0) {
                        Optional<SeriesVrExteriorResult.Color_List> first1 = vr.getResult().getColor_list().stream().filter(x -> x.getRemoteColorId() == carPhotoViewMap.getKey()).findFirst();
                        if (first1.isPresent()) {
                            if (first1.get().getColorValues() != null && !"".equals(first1.get().getColorValues())
                                    && first1.get().getColorValues().length() > 0) {
                                url = url + "&vrecolor=" + UrlUtil.encode(first1.get().getColorValues());
                            } else {
                                url = url + "&vrecolor=" + first1.get().getColorValue();
                            }
                        }
                    }
                    item.setUrl(url);
                    picDTO.getPicItems().add(item);
                }
            }
        }
        if (picDTO.getPicItems().size() == 0) {
            carPhotoViewEntityList.stream().limit(4).forEach(x -> {
                SeriesSubscribeNewsDto.PicDTO.picItemsDTO item = new SeriesSubscribeNewsDto.PicDTO.picItemsDTO();
                item.setPic(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(x.getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                item.setUrl(String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=%s", seriesDto.getId(), UrlUtil.encode(seriesDto.getName()), x.getPicClass()));
                picDTO.getPicItems().add(item);
            });
        }
        updateDB(makeParam(seriesDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.IMAGE.getType(), new Timestamp(picDTO.getPublishTime().getTime())), JSONObject.toJSONString(picDTO));
    }


    private void refreshRankList(SeriesDetailDto seriesDetailDto, Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap, Map<Integer, SeriesRankResultDto> rankMap, Consumer<String> xxlLog) {
        List<SeriesSubscribeNewsEntity> lastMonthRankList = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.RANK_MONTH.getType());
        List<SeriesSubscribeNewsEntity> lastWeekRankList = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.RANK_WEEK.getType());
        Integer seriesId = seriesDetailDto.getId();
        SeriesRankResultDto seriesRankResultDto = rankMap.get(seriesId);
        RankSaleMonthSourceEntity monthRank = seriesRankResultDto.getMonthRank();
        RankSaleWeekSourceEntity weekRank = seriesRankResultDto.getWeekRank();

        if (Objects.nonNull(monthRank)) {
            Date date = DateUtil.parse(monthRank.getMonth() + "-10 11:00:00", "yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            long time = calendar.getTimeInMillis() + monthRank.getRnnum() * 1000;
            if (!CollectionUtils.isEmpty(lastMonthRankList)) {
                SeriesSubscribeNewsEntity lastMontRank = lastMonthRankList.get(0);
                if (!DateUtil.convertTimestamp(String.valueOf(lastMontRank.getDisplay_time().getTime()),"yyyy-MM").equals(DateUtil.format(calendar.getTime(),"yyyy-MM"))) {
                    SeriesSubscribeNewsDto.RankInfoDto rankInfoDto = new SeriesSubscribeNewsDto.RankInfoDto();
                    LocalDate month = LocalDate.parse(monthRank.getMonth(), RankConstant.LOCAL_MONTH_FORMATTER);
                    SeriesSubscribeNewsDto.RankInfoDto lastMonth = JSONObject.parseObject(lastMontRank.getData(), SeriesSubscribeNewsDto.RankInfoDto.class);
                    rankInfoDto.setDate(month.getMonthValue() + "月");
                    rankInfoDto.setLastSaleCount(lastMonth.getCurrentSaleCount());
                    rankInfoDto.setCurrentSaleCount(monthRank.getSalecnt());
                    rankInfoDto.setRnnum(monthRank.getRnnum());
                    rankInfoDto.setDateValue(monthRank.getMonth());
                    rankInfoDto.setScheme(String.format(RANK_HISTORY_SCHEME_TEMPLATE, seriesDetailDto.getId(), CommonHelper.encodeUrl(seriesDetailDto.getName()), 1, 1, monthRank.getMonth(), 0));
                    updateDB(makeParam(seriesDetailDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.RANK_MONTH.getType(), new Timestamp(time)), JSONObject.toJSONString(rankInfoDto));
                }
            } else {
                SeriesSubscribeNewsDto.RankInfoDto rankInfoDto = new SeriesSubscribeNewsDto.RankInfoDto();
                LocalDate month = LocalDate.parse(monthRank.getMonth(), RankConstant.LOCAL_MONTH_FORMATTER);
                rankInfoDto.setDate(month.getMonthValue() + "月");
                rankInfoDto.setLastSaleCount(0);
                rankInfoDto.setCurrentSaleCount(monthRank.getSalecnt());
                rankInfoDto.setRnnum(monthRank.getRnnum());
                rankInfoDto.setDateValue(monthRank.getMonth());
                rankInfoDto.setScheme(String.format(RANK_HISTORY_SCHEME_TEMPLATE, seriesDetailDto.getId(), CommonHelper.encodeUrl(seriesDetailDto.getName()), 1, 1, monthRank.getMonth(), 0));
                updateDB(makeParam(seriesDetailDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.RANK_MONTH.getType(),new Timestamp(time)), JSONObject.toJSONString(rankInfoDto));
            }
        }


        // 保存周榜
        if (Objects.nonNull(weekRank)) {
            Date date = DateUtil.parse(weekRank.getWeek_day() + " 11:00:00", "yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // 给Calendar对象添加7天
            calendar.add(Calendar.DATE, 7);

            long time =calendar.getTimeInMillis() + weekRank.getRnnum() * 1000;
            LocalDate week = LocalDate.parse(weekRank.getWeek_day(), RankConstant.LOCAL_WEEK_FORMATTER);
            LocalDate weekStart = week.minusDays(1);
            LocalDate weekEnd = week.plusDays(5);
            if (!CollectionUtils.isEmpty(lastWeekRankList)) {
                SeriesSubscribeNewsEntity lastWeekRank = lastWeekRankList.get(0);
                if (!DateUtil.convertTimestamp(String.valueOf(lastWeekRank.getDisplay_time().getTime()),"yyyy-MM-dd").equals(DateUtil.format(calendar.getTime(),"yyyy-MM-dd"))) {
                    SeriesSubscribeNewsDto.RankInfoDto rankInfoDto = new SeriesSubscribeNewsDto.RankInfoDto();
                    SeriesSubscribeNewsDto.RankInfoDto lastWeek = JSONObject.parseObject(lastWeekRank.getData(), SeriesSubscribeNewsDto.RankInfoDto.class);
                    rankInfoDto.setDate(weekStart.format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER) + "-" + weekEnd.format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER));
                    rankInfoDto.setLastSaleCount(lastWeek.getCurrentSaleCount());
                    rankInfoDto.setCurrentSaleCount(weekRank.getSalecnt());
                    rankInfoDto.setRnnum(weekRank.getRnnum());
                    rankInfoDto.setDateValue(weekRank.getWeek_day());
                    rankInfoDto.setScheme(String.format(RANK_HISTORY_SCHEME_TEMPLATE, seriesDetailDto.getId(), CommonHelper.encodeUrl(seriesDetailDto.getName()), 1, 2, weekRank.getWeek_day(), 0));
                    updateDB(makeParam(seriesDetailDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.RANK_WEEK.getType(), new Timestamp(time)), JSONObject.toJSONString(rankInfoDto));
                }
            } else {
                SeriesSubscribeNewsDto.RankInfoDto rankInfoDto = new SeriesSubscribeNewsDto.RankInfoDto();
                rankInfoDto.setDate(weekStart.format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER) + "-" + weekEnd.format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER));
                rankInfoDto.setLastSaleCount(0);
                rankInfoDto.setCurrentSaleCount(weekRank.getSalecnt());
                rankInfoDto.setRnnum(weekRank.getRnnum());
                rankInfoDto.setDateValue(weekRank.getWeek_day());
                rankInfoDto.setScheme(String.format(RANK_HISTORY_SCHEME_TEMPLATE, seriesDetailDto.getId(), CommonHelper.encodeUrl(seriesDetailDto.getName()), 1, 2, weekRank.getWeek_day(), 0));
                updateDB(makeParam(seriesDetailDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.RANK_WEEK.getType(), new Timestamp(time)), JSONObject.toJSONString(rankInfoDto));
            }
        }

    }

    public Map<Integer, SeriesRankResultDto> getRankMap(String lastMonth, String lastWeek) {
        List<RankSaleMonthSourceEntity> monthRankList = rankSaleMonthSourceComponent.getSaleCountByCondition(lastMonth, lastMonth, 1000);
        List<RankSaleWeekSourceEntity> weekRankList = rankSaleWeekSourceComponent.getListByWeek(lastWeek, lastWeek, 1000);
        for (int i = 0; i < monthRankList.size(); i++) {
            RankSaleMonthSourceEntity rankSaleMonthSourceEntity = monthRankList.get(i);
            rankSaleMonthSourceEntity.setRnnum(i + 1);
        }
        for (int i = 0; i < weekRankList.size(); i++) {
            RankSaleWeekSourceEntity rankSaleWeekSourceEntity = weekRankList.get(i);
            rankSaleWeekSourceEntity.setRnnum(i + 1);
        }

        Map<String, RankSaleMonthSourceEntity> monthRankMap = monthRankList.stream().collect(Collectors.toMap(RankSaleMonthSourceEntity::getSeriesid, x -> x));
        Map<String, RankSaleWeekSourceEntity> weekRankMap = weekRankList.stream().collect(Collectors.toMap(RankSaleWeekSourceEntity::getSeriesid, x -> x));
        Map<Integer, SeriesRankResultDto> rankResultMap = new HashMap<>();
        HashSet<String> seriesIdSet = new HashSet<>(monthRankMap.keySet());
        seriesIdSet.addAll(weekRankMap.keySet());
        seriesIdSet.forEach(x -> rankResultMap.put(Integer.parseInt(x), SeriesRankResultDto.builder().monthRank(monthRankMap.get(x)).weekRank(weekRankMap.get(x)).build()));
        return rankResultMap;
    }


    @Data
    @Builder
    public static class SeriesRankResultDto {
        private RankSaleMonthSourceEntity monthRank;
        private RankSaleWeekSourceEntity weekRank;
    }


    /**
     * 上市价格公布
     */
    void refreshSeriesPrice(SeriesEntity seriesDto, Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap, SeriesDetailDto seriesDetailDto) {

        if (seriesDetailDto.getOnLineTime() == null) {
            return;
        }
        BaseModel<CmsArticleDataResult> articleData = cmsApiClient.getCmsArticleData(seriesDto.getId(), 300001).join();

        SeriesSubscribeNewsDto.PriceDto priceDto = new SeriesSubscribeNewsDto.PriceDto();
        priceDto.setPublishTime(seriesDetailDto.getOnLineTime());
        priceDto.setPrice(seriesDetailDto.getPrice());
        priceDto.setSpecNum(seriesDetailDto.getSellSpecNum() + seriesDetailDto.getWaitSpecNum() + seriesDetailDto.getStopSpecNum());

        if (articleData != null && articleData.getResult() != null && articleData.getResult().getItems() != null && articleData.getResult().getItems().size() > 0) {
            CmsArticleDataResult.ItemsDTO itemsDTO = articleData.getResult().getItems().get(0);
            priceDto.setSource("之家原创");
            priceDto.setAuthorName(itemsDTO.getMainDataAll().getCms_editor_name());
            priceDto.setBizId(itemsDTO.getBizId());
            priceDto.setMainDataType(itemsDTO.getMainDataAll().getMain_data_type());
            priceDto.setTitle(StringUtils.isNotEmpty(itemsDTO.getMainDataAll().getSmall_title()) ? itemsDTO.getMainDataAll().getSmall_title() : itemsDTO.getMainDataAll().getTitle());

            if (CollectionUtils.isEmpty(itemsDTO.getMainDataAll().getMulti_images())) {
                priceDto.setImgUrlList(Collections.singletonList(itemsDTO.getMainDataAll().getImg_url()));
            }else {
                priceDto.setImgUrlList(itemsDTO.getMainDataAll().getMulti_images());
            }
            priceDto.setScheme("autohome://article/articledetail?newsid=" + itemsDTO.getBizId() + "&newstype=0");
        }

        List<SeriesSubscribeNewsEntity> lastPrice = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.MARKET_PRICE.getType());
        SeriesSubscribeNewsDto.PriceDto lastPriceData = null;
        if (!CollectionUtils.isEmpty(lastPrice)) {
            if (lastPrice.size() == 1) {
                lastPriceData = JSONObject.parseObject(lastPrice.get(0).getData(), SeriesSubscribeNewsDto.PriceDto.class);
            } else {
                lastPriceData = lastPrice.stream().map(x -> JSONObject.parseObject(x.getData(), SeriesSubscribeNewsDto.PriceDto.class)).max(Comparator.comparing(SeriesSubscribeNewsDto.PriceDto::getPublishTime)).get();
            }
        }
        if (lastPriceData == null || (!lastPriceData.equals(priceDto) && priceDto.getPublishTime().after(lastPriceData.getPublishTime()))) {
            updateDB(makeParam(seriesDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.MARKET_PRICE.getType(), new Timestamp(priceDto.getPublishTime().getTime())), JSONObject.toJSONString(priceDto));
        }
    }

    /**
     * 提车作业分享（仅限全新车系）
     */
    void refreshSeriesFriendShare(SeriesEntity seriesDto, Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap, SeriesDetailDto seriesDetailDto) {
        if (seriesDetailDto.getIsNewCar() || seriesDetailDto.getNewBrandTagId() == 1) {
            clubApiClient.GetTopicListByContentType(seriesDto.getId(), 1076,1,1).thenAccept(clubTopicResult -> {
                if (clubTopicResult != null && clubTopicResult.getResult() != null && clubTopicResult.getResult().getList() != null && clubTopicResult.getResult().getList().size() > 0) {
                    TopicContentResult.ListDTO listDTO = clubTopicResult.getResult().getList().get(0);

                    SeriesSubscribeNewsDto.friendShareDTO friendShareDTO = new SeriesSubscribeNewsDto.friendShareDTO();
                    friendShareDTO.setPublishTime(DateUtil.parse(listDTO.getPostdate(), "yyyy-MM-dd HH:mm:ss"));
                    BaseModel<List<UserInfoResult>> userInfo = userApiClient.getUserInfoList(listDTO.getPost_memberid()).join();
                    if (null != userInfo && null != userInfo.getResult() && 0 == userInfo.getReturncode() && !CollectionUtils.isEmpty(userInfo.getResult())) {
                        friendShareDTO.setAuthorName(userInfo.getResult().get(0).getNewnickname());
                    } else {
                        friendShareDTO.setAuthorName(listDTO.getPost_membername());
                    }
                    friendShareDTO.setTitle(listDTO.getTitle());
                    friendShareDTO.setScheme(String.format("autohome://club/topicdetail?topicid=%d&bbsid=%d&from=10", listDTO.getTopicid(), seriesDto.getId()));
                    friendShareDTO.setTopicid(listDTO.getTopicid());

                    if (listDTO.getVideoinfo() != null) {
                        friendShareDTO.setIsvideo(true);
                        friendShareDTO.setDuration(listDTO.getVideoinfo().getDuration());
                        friendShareDTO.getImgUrlList().add(ImageUtils.convertImageUrl(listDTO.getVideoinfo().getVideoimg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                    } else {
                        if (StringUtils.isNotEmpty(listDTO.getImgs())) {
                            String[] arrImgs = listDTO.getImgs().split(",");
                            if (arrImgs != null && arrImgs.length > 0) {
                                for (String imgurl : arrImgs) {
                                    if (friendShareDTO.getImgUrlList().size() < 3) {
                                        friendShareDTO.getImgUrlList().add(ImageUtils.convertImageUrl(imgurl, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                                    }
                                }
                            }
                        }
                    }
                    List<SeriesSubscribeNewsEntity> last = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.CAR_WORK.getType());
                    SeriesSubscribeNewsDto.friendShareDTO lastFriendSharData = null;
                    if (!CollectionUtils.isEmpty(last)) {
                        if (last.size() == 1) {
                            lastFriendSharData = JSONObject.parseObject(last.get(0).getData(), SeriesSubscribeNewsDto.friendShareDTO.class);
                        } else {
                            lastFriendSharData = last.stream().map(x -> JSONObject.parseObject(x.getData(), SeriesSubscribeNewsDto.friendShareDTO.class)).max(Comparator.comparing(SeriesSubscribeNewsDto.friendShareDTO::getPublishTime)).get();
                        }
                    }
                    if (lastFriendSharData == null || lastFriendSharData.getTopicid().compareTo(friendShareDTO.getTopicid())!=0 && friendShareDTO.getPublishTime().after(lastFriendSharData.getPublishTime())) {
                        BaseModel<List<UserAuthSeriesResult>> authCar = userApiClient.getUserAuthseries(listDTO.getPost_memberid()).join();
                        boolean hasAuthCar = null != authCar && null != authCar.getResult() && 0 == authCar.getReturncode() && !CollectionUtils.isEmpty(authCar.getResult());
                        if (hasAuthCar && authCar.getResult().get(0).getList() != null) {
                            UserAuthSeriesResult.AuthseriesResult authseriesResult = authCar.getResult().get(0).getList().stream().filter(x -> seriesDto.getId() == x.getSeriesId()).findFirst().orElse(null);
                            friendShareDTO.setCarowner(authseriesResult != null);
                        }
                        updateDB(makeParam(seriesDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.CAR_WORK.getType(), new Timestamp(friendShareDTO.getPublishTime().getTime())), JSONObject.toJSONString(friendShareDTO));
                    }
                }
            }).exceptionally(e -> null).join();
        }
    }

    /**
     * 车系参配
     */
    void refreshSeriesParam(SeriesDetailDto seriesDetailDto, Map<Integer, List<SeriesSubscribeNewsEntity>> lastNewsGrpByBizTypeMap,List<SpecEntity> specEntityList){
        specEntityList=specEntityList.stream()
                .filter(x -> x.getParamIsShow() == 1 && x.getIsImageSpec() == 0 && x.getDtime() != null && x.getState() != 40)
                .sorted(Comparator.comparing(specEntity -> DateUtil.parse(((SpecEntity) specEntity).getDtime(), "yyyy-MM-dd HH:mm:ss")).reversed())
                .limit(20)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(specEntityList)) {
            return;
        }
        List<SpecParamEntity> specPartParam1BySeriesId = specParamMapper.getSpecPartParam1BySeriesId(seriesDetailDto.getId(), seriesDetailDto.getLevelId());

        if (CollectionUtils.isEmpty(specPartParam1BySeriesId)) {
            return;
        }
        Map<Integer, List<SpecParamEntity>> specParamMap = specPartParam1BySeriesId.stream().collect(Collectors.groupingBy(SpecParamEntity::getSpecId));

        //油车\纯电\非纯电
        String energyName = seriesDetailDto.getEnergytype() == 1 ? ("4".equals(seriesDetailDto.getFueltypes()) ? "纯电" : "非纯电") : "油车";

        SeriesSubscribeNewsDto.CarParamDTO carParamDTO=new SeriesSubscribeNewsDto.CarParamDTO();

        specEntityList.forEach(x -> {
            if (specParamMap.containsKey(x.getId())) {
                SeriesSubscribeNewsDto.CarParamDTO.SpecconfigitemsDTO specconfigitemsDTO = new SeriesSubscribeNewsDto.CarParamDTO.SpecconfigitemsDTO();
                specconfigitemsDTO.setSpecid(x.getId());
                specconfigitemsDTO.setSpecname(x.getName());
                //specconfigitemsDTO.setUrl(String.format("autohome://carcompare/specsummaryconfig?seriesid=%s&specid=%s&specname=%s", seriesDetailDto.getId(), x.getId(), UrlUtil.encode(x.getName())));

                List<SpecParamEntity> paramEntities = specParamMap.get(x.getId());
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(1, paramEntities));
                if (seriesDetailDto.getEnergytype() == 1) {
                    specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(50, paramEntities));
                    specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(17, paramEntities));
                    specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(10000, paramEntities));
                } else {
                    specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(102, paramEntities));
                    specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(50, paramEntities));
                    specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(17, paramEntities));
                }
                carParamDTO.getSpecconfigitems().add(specconfigitemsDTO);
            }
        });
        if (carParamDTO.getSpecconfigitems().size()==0) {
            return;
        }
        carParamDTO.setSpeccount(carParamDTO.getSpecconfigitems().size());
        carParamDTO.setUrl(String.format("autohome://carcompare/paramcontrast?seriesid=%s&seriesname=%s", seriesDetailDto.getId(), UrlUtil.encode(seriesDetailDto.getName())));

        List<SeriesSubscribeNewsEntity> lastParamList = lastNewsGrpByBizTypeMap.get(SeriesSubscribeNewsEnum.CONFIG.getType());
        SeriesSubscribeNewsDto.CarParamDTO lastParamDto = null;
        SeriesSubscribeNewsEntity lastParam = null;
        if (!CollectionUtils.isEmpty(lastParamList)) {
            lastParam = lastParamList.get(0);
            lastParamDto = JSONObject.parseObject(lastParam.getData(), SeriesSubscribeNewsDto.CarParamDTO.class);
        }
        // 限制只有更新时间晚于上次最后一条数据才更新
        if (lastParamDto==null||lastParamDto.getSpeccount()!= carParamDTO.getSpeccount() && specPartParam1BySeriesId.get(0).getModified_Stime().after(lastParam.getDisplay_time())) {
            updateDB(makeParam(seriesDetailDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.CONFIG.getType(),specPartParam1BySeriesId.get(0).getModified_Stime()), JSONObject.toJSONString(carParamDTO));
        }
    }
    SeriesSubscribeNewsDto.CarParamDTO.SpecconfigitemsDTO.ConfiglistDTO getSpecConfigItem(int paramId, List<SpecParamEntity> paramEntities) {
        SeriesSubscribeNewsDto.CarParamDTO.SpecconfigitemsDTO.ConfiglistDTO item = new SeriesSubscribeNewsDto.CarParamDTO.SpecconfigitemsDTO.ConfiglistDTO();
        item.setParamvalue("-");
        switch (paramId) {
            case 1:
                item.setParamname("最高车速(km/h)");
                break;
            case 50:
                item.setParamname("最大功率(kW)");
                break;
            case 17:
                item.setParamname("轴距(mm)");
                break;
            case 102:
                item.setParamname("油耗(L/100km)");
                break;
            case 49:
                item.setParamname("最大马力(Ps)");
                break;
            case 10000:
                item.setParamname("座位数(个)");
                break;
        }
        if (org.apache.dubbo.common.utils.CollectionUtils.isNotEmpty(paramEntities)) {
            if (paramId == 10000) {
                item.setParamvalue(paramEntities.get(0).getSeats());
            } else {
                Optional<SpecParamEntity> first = paramEntities.stream().filter(x -> x.getParamId() == paramId && !"-".equals(x.getParamValue())).findFirst();
                if (first.isPresent()) {
                    item.setParamvalue(first.get().getParamValue());
                }
            }
        }
        return item;
    }
}

