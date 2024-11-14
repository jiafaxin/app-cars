package com.autohome.app.cars.service.components.hangqing;

import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.common.BasePageModel;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.Level;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleMonthSourceEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesSpecComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesSpecDto;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.hangqing.dtos.CitySortHangqingDto;
import com.autohome.app.cars.service.components.hangqing.dtos.SeriesSortConditionDto;
import com.autohome.app.cars.service.components.hangqing.dtos.SpecCityPriceHisDto;
import com.autohome.app.cars.service.components.recrank.attention.AreaSeriesAttentionComponent;
import com.autohome.app.cars.service.components.recrank.attention.dtos.AreaSeriesAttentionDto;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleMonthSourceComponent;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/6/20
 */
@Slf4j
@Component
@DBConfig(tableName = "city_sort_hangqing")
public class CitySortHangqingComponent extends BaseComponent<CitySortHangqingDto> {

    @Autowired
    private SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private RankSaleMonthSourceComponent rankSaleMonthSourceComponent;

    @Autowired
    private AreaSeriesAttentionComponent areaSeriesAttentionComponent;

    @Autowired
    private DealerApiClient dealerApiClient;

    @Autowired
    private SpecCityPriceHisComponent specCityPriceHisComponent;

    @Autowired
    private SeriesSpecComponent seriesSpecComponent;

    final static String sortParamName = "sort";
    final static String cityParamName = "cityId";

    @Value("${hangqing_overall_sort_price_off_threshold:0.3}")
    private double overallSortPriceOffThreshold;

    @Value("${hangqing_price_off_recently_threshold:0.015}")
    private double priceOffRecentlyThreshold;

    @Value("${hangqing_important_price_off_recently_threshold:0.05}")
    private double importantPriceOffRecentlyThreshold;


    TreeMap<String, Object> makeParam(int cityId, int sort) {
        return ParamBuilder.create(cityParamName, cityId).add(sortParamName, sort).build();
    }

    public CompletableFuture<CitySortHangqingDto> getAsync(int cityId, int sort) {
        return baseGetAsync(makeParam(cityId, sort));
    }

    public CitySortHangqingDto get(int cityId, int sort) {
        return baseGet(makeParam(cityId, sort));
    }

    // TODO chengjincheng 2024/7/30 测试临时方法
    @Deprecated
    public void clearCache(int cityId) {
        delete(makeParam(cityId, 5));
    }

    public void refreshAllCity(int cityId, Consumer<String> xxlLog) {
        System.out.println(new Date());
        xxlLog.accept("begin");
        List<SeriesSortConditionDto> sortDtoList = null;
        try {
            sortDtoList = getSeriesSortDtoList(cityId);
        } catch (Exception e) {
            log.error("准备数据获取异常", e);
        }

        if (!CollectionUtils.isEmpty(sortDtoList)) {
            try {
                handle(sortDtoList, cityId);
            } catch (Exception e) {
                log.error("计算任务执行异常", e);
            }
        }
        System.out.println("end" + new Date());
    }

    public void refreshAll(Consumer<String> xxlLog) {
        CityUtil.getAllCityIds().forEach(cityId -> {
            try {
                List<SeriesSortConditionDto> sortDtoList = getSeriesSortDtoList(cityId);
                if (!CollectionUtils.isEmpty(sortDtoList)) {
                    handle(sortDtoList, cityId);
                    xxlLog.accept(String.format("城市%s%s", CityUtil.getCityName(cityId), cityId) + "数据更新成功");
                }
            } catch (Exception e) {
                xxlLog.accept(String.format("城市%s%s", CityUtil.getCityName(cityId), cityId) + "数据更新失败"
                        + Arrays.toString(e.getStackTrace()));
            }
        });
    }


    public void handle(List<SeriesSortConditionDto> sortDtoList, int cityId) {
        // 【近期降价】排序（综合排序会使用到近期降价的结果）
        CitySortHangqingDto priceOffRecentlyDto = getDtoByPriceOffRecentlySort(sortDtoList, cityId);
        List<Integer> priceOffRecentlySeriesIdList = Collections.emptyList();
        if (Objects.nonNull(priceOffRecentlyDto) && !CollectionUtils.isEmpty(priceOffRecentlyDto.getDtoList())) {
            update(makeParam(cityId, 5), priceOffRecentlyDto);
            priceOffRecentlySeriesIdList = priceOffRecentlyDto.getDtoList().stream()
                    .map(CitySortHangqingDto.HangqingDto::getSeriesId)
                    .toList();
        } else {
            delete(makeParam(cityId, 5));
        }
        // 综合排序
        List<SeriesSortConditionDto> overallSortList = geListByOverallSort(sortDtoList, priceOffRecentlySeriesIdList);
        if (!CollectionUtils.isEmpty(overallSortList)) {
            update(makeParam(cityId, 1), buildHangqingDto(overallSortList));
        }
        // 降幅大排序
        List<SeriesSortConditionDto> priceOffSortList = getListByPriceOffSort(sortDtoList);
        if (!CollectionUtils.isEmpty(priceOffSortList)) {
            update(makeParam(cityId, 2), buildHangqingDto(priceOffSortList));
        }
        // 月销量排序
        List<SeriesSortConditionDto> monthSaleSortList = getListByMonthSaleSort(sortDtoList);
        if (!CollectionUtils.isEmpty(monthSaleSortList)) {
            update(makeParam(cityId, 3), buildHangqingDto(monthSaleSortList));
        }
        // 关注度排序
        List<SeriesSortConditionDto> attentionSortList = getListByAttentionSort(sortDtoList);
        if (!CollectionUtils.isEmpty(attentionSortList)) {
            update(makeParam(cityId, 4), buildHangqingDto(attentionSortList));
        }
        // 【近期降价-重要降价车系】排序
        CitySortHangqingDto importantPriceOffRecentlyDto = getDtoByImportantPriceOffRecentlySort(sortDtoList, cityId);
        if (Objects.nonNull(importantPriceOffRecentlyDto)) {
            update(makeParam(cityId, 51), importantPriceOffRecentlyDto);
        } else {
            delete(makeParam(cityId, 51));
        }
        // 历史新低
        CitySortHangqingDto priceHisDto = getListByPriceHisSort(sortDtoList, cityId);
        if (Objects.nonNull(priceHisDto) && !CollectionUtils.isEmpty(priceHisDto.getDtoList())) {
            update(makeParam(cityId, 6), priceHisDto);
        } else {
            delete(makeParam(cityId, 6));
        }
    }


    private CitySortHangqingDto buildHangqingDto(List<SeriesSortConditionDto> sortDtoList) {
        CitySortHangqingDto dto = new CitySortHangqingDto();
        dto.setDtoList(sortDtoList.stream().map(e -> {
            CitySortHangqingDto.HangqingDto hangqingDto = new CitySortHangqingDto.HangqingDto();
            hangqingDto.setSeriesId(e.getSeriesId());
            hangqingDto.setBrandId(e.getBrandId());
            hangqingDto.setLevelId(e.getLevelId());
            hangqingDto.setMinPrice(e.getSeriesMinPrice());
            hangqingDto.setMaxPrice(e.getSeriesMaxPrice());
            hangqingDto.setEnergyType(e.getEnergytype());
            hangqingDto.setFuelTypeDetail(e.getFuelTypes());
            return hangqingDto;
        }).toList());
        return dto;
    }


    /**
     * 获取待排序的车系列表
     *
     * @param cityId
     * @return
     */
    public List<SeriesSortConditionDto> getSeriesSortDtoList(int cityId) {
        // 对应城市有经销商报价的车系id
        List<Integer> seriesIdList = seriesCityAskPriceNewComponent.getSeriesByCity(cityId);
        List<SeriesSortConditionDto> sortDtoList = new ArrayList<>();

        buildSeriesBaseInfo(cityId, seriesIdList, sortDtoList);
        buildSpecPriceHis(sortDtoList, cityId);
        buildAttention(sortDtoList);
        getMonthSale(sortDtoList);

        // TODO chengjincheng 2024/7/30 测试支持 临时增加
        buildTemp(sortDtoList);
        return sortDtoList;
    }

    private void buildTemp(List<SeriesSortConditionDto> sortDtoList) {
        List<CitySortHangqingDto.HangqingDto> resultList = new ArrayList<>();
        List<CitySortHangqingDto.HangqingDto> finalResultList = resultList;
        sortDtoList.forEach(dto -> {
            try {
                // TODO chengjincheng 2024/7/29 此处需要review
                if (!CollectionUtils.isEmpty(dto.getSpecPriceHisList())) {
                    List<CitySortHangqingDto.SpecPriceOffInfo> specPriceOffInfoList = new ArrayList<>();
                    dto.getSpecPriceHisList().forEach(spec -> {
                        if (!CollectionUtils.isEmpty(spec.getDtoList())) {
                            List<SeriesSortConditionDto.SpecCityPriceHisDto> hisDtoList = spec.getDtoList();
                            String dt = "";
                            boolean priceUp = false;
                            int priceOff = 0;
                            double priceOffRecently = 0D;
                            for (int i = hisDtoList.size() - 1; i >= 1; i--) {
                                int dealerPriceOff = hisDtoList.get(i - 1).getNewsPrice() - hisDtoList.get(i).getNewsPrice();
                                if ((double) dealerPriceOff / hisDtoList.get(i - 1).getNewsPrice() > 0.015D) {
                                    dt = hisDtoList.get(i).getDate();
                                    priceOff = hisDtoList.get(i - 1).getNewsPrice() - hisDtoList.get(i).getNewsPrice();
                                    priceOffRecently = (double) dealerPriceOff / hisDtoList.get(i - 1).getNewsPrice();
                                    break;
                                } else if (hisDtoList.get(i).getNewsPrice() > hisDtoList.get(i - 1).getNewsPrice()) {
                                    // 涨价了
                                    priceUp = true;
                                    break;
                                }
                            }
                            if (priceUp
                                    || StringUtils.isEmpty(dt)
                                    || getDiffDays(dt, DateFormatUtils.format(new Date(), "yyyy-MM-dd")) > 14) {
                            } else if (StringUtils.isNotEmpty(dt) && priceOff > 0) {
                                spec.setPriceOffRecently(priceOffRecently);
                            }
                        }
                    });
                    if (!CollectionUtils.isEmpty(specPriceOffInfoList)) {
                        CitySortHangqingDto.PriceOffInfo priceOffInfo = new CitySortHangqingDto.PriceOffInfo();
                        String startDt = specPriceOffInfoList.stream().map(CitySortHangqingDto.SpecPriceOffInfo::getDt).sorted(Comparator.reverseOrder()).findFirst().get();
                        priceOffInfo.setStartDt(startDt);
                        priceOffInfo.setSpecPriceOffInfoList(specPriceOffInfoList);
                        CitySortHangqingDto.HangqingDto hangqingDto = buildPriceOffDtoList(dto, priceOffInfo);
                        finalResultList.add(hangqingDto);
                    }
                }
            } catch (Exception e) {
                log.error("降价车系计算错误", e);
            }
        });
    }

    private void buildSpecPriceHis(List<SeriesSortConditionDto> sortDtoList, int cityId) {
        sortDtoList.forEach(dto -> {
            SeriesSpecDto seriesSpecDto = seriesSpecComponent.get(dto.getSeriesId());
            if (Objects.nonNull(seriesSpecDto)
                    && !CollectionUtils.isEmpty(seriesSpecDto.getItems())) {
                List<SeriesSpecDto.Item> specList = seriesSpecDto.getItems().stream()
                        .filter(e -> e.getState() == 20 || e.getState() == 30)
                        .toList();
                specList.forEach(spec -> {
                    List<SpecCityPriceHisDto> dtoList = specCityPriceHisComponent.get(spec.getId(), cityId);
                    if (!CollectionUtils.isEmpty(dtoList)) {
                        SeriesSortConditionDto.SpecCityPriceHis his = new SeriesSortConditionDto.SpecCityPriceHis();
                        his.setSpecId(spec.getId());
                        his.setDtoList(dtoList.stream().map(e -> {
                            SeriesSortConditionDto.SpecCityPriceHisDto hisDto = new SeriesSortConditionDto.SpecCityPriceHisDto();
                            hisDto.setDate(e.getDate());
                            hisDto.setNewsPrice(e.getNewsPrice());
                            return hisDto;
                        }).toList());
                        // TODO chengjincheng 2024/7/30 最大降幅判断
                        if (spec.getMinPrice() > 0 && his.getDtoList().get(dtoList.size() - 1).getNewsPrice() > 0) {
                            double priceOff = (double) (spec.getMinPrice() - his.getDtoList().get(dtoList.size() - 1).getNewsPrice())
                                    / spec.getMinPrice();
                            dto.setMaxPriceOff(Math.max(priceOff, dto.getMaxPriceOff()));
                        }
                        dto.getSpecPriceHisList().add(his);
                    }
                });
            }
        });
    }

    /**
     * 过滤并组装基本车系信息
     *
     * @param cityId       城市id
     * @param seriesIdList 车系id集合
     * @param sortDtoList  待排序dto
     */
    private void buildSeriesBaseInfo(int cityId, List<Integer> seriesIdList, List<SeriesSortConditionDto> sortDtoList) {
        // 商用车levelId
        List<Integer> commercialSeriesLevelIds = Arrays.asList(11, 12, 13, 14, 25);
        List<List<Integer>> seriesIdListPart = Lists.partition(seriesIdList, 100);
        // 过滤出乘用车和在售车系
        seriesIdListPart.forEach(seriesIdPart -> {
            List<SeriesCityAskPriceDto> seriesCityAskPriceDtoList =
                    seriesCityAskPriceNewComponent.get(seriesIdPart, cityId).join();
            Map<Integer, SeriesCityAskPriceDto> seriesCityAskPriceDtoMap = seriesCityAskPriceDtoList.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(SeriesCityAskPriceDto::getSeriesId, Function.identity(), (k1, k2) -> k2));
            List<SeriesDetailDto> seriesDetailDtoList = seriesDetailComponent.getList(seriesIdPart).join().stream()
                    .filter(Objects::nonNull)
                    .filter(e -> !commercialSeriesLevelIds.contains(e.getLevelId())
                            && Arrays.asList(20, 30).contains(e.getState()))
                    .filter(e -> Objects.nonNull(seriesCityAskPriceDtoMap.get(e.getId()))
                            && seriesCityAskPriceDtoMap.get(e.getId()).isLocalPrice(cityId))
                    .toList();
            if (!CollectionUtils.isEmpty(seriesDetailDtoList)) {
                seriesDetailDtoList.forEach(seriesDetailDto -> {
                    SeriesSortConditionDto seriesSortConditionDto = new SeriesSortConditionDto();
                    seriesSortConditionDto.setSeriesId(seriesDetailDto.getId());
                    seriesSortConditionDto.setBrandId(seriesDetailDto.getBrandId());
                    seriesSortConditionDto.setLevelId(seriesDetailDto.getLevelId());
                    seriesSortConditionDto.setFuelTypes(seriesDetailDto.getFueltypes());
                    seriesSortConditionDto.setSeriesMinPrice(seriesDetailDto.getMinPrice());
                    seriesSortConditionDto.setSeriesMaxPrice(seriesDetailDto.getMaxPrice());
                    seriesSortConditionDto.setEnergytype(seriesDetailDto.getEnergytype());
                    sortDtoList.add(seriesSortConditionDto);
                });
            }
        });
    }

    /**
     * 获取车系关注度
     *
     * @param seriesList 车系集合
     */
    private void buildAttention(List<SeriesSortConditionDto> seriesList) {
        try {
            if (seriesList != null && !seriesList.isEmpty()) {
                //获取全国的车系关注度
                List<AreaSeriesAttentionDto> seriesAttList = areaSeriesAttentionComponent.get(-1);
                Map<Integer, Long> seriesAttMap = seriesAttList.stream()
                        .collect(Collectors.toMap(AreaSeriesAttentionDto::getSeriesId, AreaSeriesAttentionDto::getAtt));
                if (!CollectionUtils.isEmpty(seriesAttList)) {
                    for (SeriesSortConditionDto item : seriesList) {
                        Long att = seriesAttMap.get(item.getSeriesId());
                        if (att != null) {
                            item.setAttention(att.intValue());
                        } else {
                            //如果无热度, 设为0
                            item.setAttention(0);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getAttention异常-ex:", ex);
        }
    }

    /**
     * 获取车系月销量
     *
     * @param seriesList 车系集合
     */
    private void getMonthSale(List<SeriesSortConditionDto> seriesList) {
        try {
            if (seriesList != null && !seriesList.isEmpty()) {
                List<RankSaleMonthSourceEntity> rankSaleMonthSourceEntityList = rankSaleMonthSourceComponent.getSaleCountByCondition("", "", 10000);
                if (Objects.nonNull(rankSaleMonthSourceEntityList) && !rankSaleMonthSourceEntityList.isEmpty()) {
                    for (SeriesSortConditionDto item : seriesList) {
                        RankSaleMonthSourceEntity entity = rankSaleMonthSourceEntityList.stream().filter(p -> p.getSeriesid().equals(item.getSeriesId() + "")).findFirst().orElse(null);
                        if (entity != null && entity.getSalecnt() != null) {
                            item.setMonthSaleNum(entity.getSalecnt());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getMonthSale异常-ex:", ex);
        }
    }


    /**
     * 综合排序
     *
     * @param sortDtoList 待排序dto
     * @return 排序后的dto
     */
    private List<SeriesSortConditionDto> geListByOverallSort(List<SeriesSortConditionDto> sortDtoList,
                                                             List<Integer> priceOffRecentlySeriesIdList) {
        // 近期降价的车系
        List<SeriesSortConditionDto> priceOffRecentlySortDtoList = new ArrayList<>(sortDtoList).stream()
                .filter(e -> priceOffRecentlySeriesIdList.contains(e.getSeriesId()))
                .toList();

        // 降幅大于30%的车系
        List<SeriesSortConditionDto> priceOffSortDtoList = new ArrayList<>(sortDtoList);
        try {
            // 过滤出降幅大于30%的车系(并且排除掉已经在进行降价列表中的车系id)，按照关注度降序
            priceOffSortDtoList = priceOffSortDtoList.stream()
                    .filter(e -> !priceOffRecentlySeriesIdList.contains(e.getSeriesId()))
                    .filter(e -> {
                        if (e.getMaxPriceOff() > 0) {
                            return e.getMaxPriceOff() > overallSortPriceOffThreshold;
                        } else {
                            return false;
                        }
                    })
                    .toList();

            // 剩余的也按照车系关注度降序
            List<SeriesSortConditionDto> attentionSortDtoList = new ArrayList<>(sortDtoList);
            List<Integer> priceOffSortSeriesIdList = priceOffSortDtoList.stream()
                    .map(SeriesSortConditionDto::getSeriesId).toList();
            attentionSortDtoList = attentionSortDtoList.stream()
                    .filter(e -> !priceOffSortSeriesIdList.contains(e.getSeriesId())
                            && !priceOffRecentlySeriesIdList.contains(e.getSeriesId()))
                    .sorted(Comparator.comparing(SeriesSortConditionDto::getAttention).reversed())
                    .toList();

            List<SeriesSortConditionDto> overallSortDtoList = new ArrayList<>();
            overallSortDtoList.addAll(priceOffSortDtoList);
            overallSortDtoList.addAll(priceOffRecentlySortDtoList);
            overallSortDtoList = new ArrayList<>(overallSortDtoList.stream()
                    .sorted(Comparator.comparing(SeriesSortConditionDto::getAttention).reversed())
                    .toList());
            overallSortDtoList.addAll(attentionSortDtoList);
            return overallSortDtoList;
        } catch (Exception ex) {
            log.error("geListByOverallSort异常-ex:", ex);
        }
        return Collections.emptyList();
    }


    /**
     * 降幅大排序
     *
     * @param sortDtoList 待排序dto
     * @return 排序后的dto
     */
    private static List<SeriesSortConditionDto> getListByPriceOffSort(List<SeriesSortConditionDto> sortDtoList) {
        List<SeriesSortConditionDto> dtoList = new ArrayList<>(sortDtoList);
        try {
            return dtoList.stream().sorted((o1, o2) -> {
                // 都有降幅，比较降幅
                double priceOff = o1.getMaxPriceOff() - o2.getMaxPriceOff();
                if (priceOff == 0) {
                    // 降幅如果相同，按照关注度排序
                    return o2.getAttention() - o1.getAttention();
                }
                // 降幅不同则按照降幅大小倒序
                return priceOff > 0 ? -1 : 1;
            }).toList();
        } catch (Exception ex) {
            log.error("getListByPriceOffSort异常-ex:", ex);
        }
        return sortDtoList;
    }


    /**
     * 获取关注度排名后列表数据
     *
     * @param sortDtoList
     * @return
     */
    private List<SeriesSortConditionDto> getListByAttentionSort(List<SeriesSortConditionDto> sortDtoList) {
        List<SeriesSortConditionDto> resultList = new ArrayList<>(sortDtoList);
        try {
            if (!resultList.isEmpty()) {
                // 根据关注度值排序
                resultList = resultList.stream()
                        .sorted(Comparator.comparing(SeriesSortConditionDto::getAttention).reversed())
                        .collect(Collectors.toList());
                return resultList;
            }
        } catch (Exception ex) {
            log.error("getListByAttentionSort异常-ex:", ex);
        }
        return sortDtoList;
    }


    /**
     * 获取月销量排名后列表数据
     *
     * @param sortDtoList
     * @return
     */
    private List<SeriesSortConditionDto> getListByMonthSaleSort(List<SeriesSortConditionDto> sortDtoList) {
        List<SeriesSortConditionDto> resultList = new ArrayList<>(sortDtoList);
        try {
            if (!resultList.isEmpty()) {
                resultList.sort((p1, p2) -> {
                    if (p1.getMonthSaleNum() == p2.getMonthSaleNum()) {
                        return Integer.compare(p2.getAttention(), p1.getAttention());
                    } else {
                        return Integer.compare(p2.getMonthSaleNum(), p1.getMonthSaleNum());
                    }
                });
                return resultList;
            }
        } catch (Exception ex) {
            log.error("getListByMonthSaleSort异常-ex:", ex);
        }
        return sortDtoList;
    }

    private CitySortHangqingDto getDtoByPriceOffRecentlySort(List<SeriesSortConditionDto> sortDtoList,
                                                             int cityId) {
        try {
            PriceOffRecentParam param = new PriceOffRecentParam();
            param.setSortId(5);
            param.setPriceOffThreshold(priceOffRecentlyThreshold);
            param.setImportantFlag(false);
            param.setDays(14);
            return getPriceOffRecentlyDto(sortDtoList, cityId, param);
        } catch (Exception ex) {
            log.error("getDtoByPriceOffRecentlySort异常-ex:", ex);
        }
        return null;
    }

    private CitySortHangqingDto getDtoByImportantPriceOffRecentlySort(List<SeriesSortConditionDto> sortDtoList,
                                                                      int cityId) {
        try {
            List<AreaSeriesAttentionDto> attentionDtoList = areaSeriesAttentionComponent.get(-1);
            List<Integer> importantSeriesIdList = attentionDtoList.stream()
                    .map(AreaSeriesAttentionDto::getSeriesId)
                    .limit(100)
                    .toList();
            PriceOffRecentParam param = new PriceOffRecentParam();
            param.setSortId(51);
            param.setPriceOffThreshold(importantPriceOffRecentlyThreshold);
            param.setImportantFlag(true);
            param.setImportantSeriesIdList(importantSeriesIdList);
            param.setDays(7);
            return getPriceOffRecentlyDto(sortDtoList, cityId, param);
        } catch (Exception ex) {
            log.error("getDtoByImportantPriceOffRecentlySort异常-ex:", ex);
        }
        return null;
    }

    @Data
    private static class PriceOffRecentParam {
        // 5-近期降价 51-重要降价车系 52-非重要降价车系
        int sortId;
        double priceOffThreshold;
        boolean importantFlag;
        List<Integer> importantSeriesIdList;
        int days;
    }

    private CitySortHangqingDto getPriceOffRecentlyDto(List<SeriesSortConditionDto> sortDtoList,
                                                       int cityId,
                                                       PriceOffRecentParam param) {
        // 待处理的dto数据
        List<SeriesSortConditionDto> dtoList = new ArrayList<>(sortDtoList);
        if (param.isImportantFlag()) {
            dtoList = dtoList.stream().filter(e -> param.getImportantSeriesIdList().contains(e.getSeriesId()))
                    .toList();
        }
        Map<Integer, SeriesSortConditionDto> conditionDtoMap = dtoList.stream()
                .collect(Collectors.toMap(SeriesSortConditionDto::getSeriesId, e -> e));


        List<CitySortHangqingDto.HangqingDto> resultList = new ArrayList<>();
        List<CitySortHangqingDto.HangqingDto> finalResultList = resultList;
        dtoList.forEach(dto -> {
            try {
                // TODO chengjincheng 2024/7/29 此处需要review
                if (!CollectionUtils.isEmpty(dto.getSpecPriceHisList())) {
                    List<CitySortHangqingDto.SpecPriceOffInfo> specPriceOffInfoList = new ArrayList<>();
                    dto.getSpecPriceHisList().forEach(spec -> {
                        if (!CollectionUtils.isEmpty(spec.getDtoList())) {
                            List<SeriesSortConditionDto.SpecCityPriceHisDto> hisDtoList = spec.getDtoList().stream()
                                    .filter(e -> e.getNewsPrice() > 0)
                                    .toList();
                            String dt = "";
                            boolean priceUp = false;
                            int priceOff = 0;
                            if (hisDtoList.size() >= 2) {
                                for (int i = hisDtoList.size() - 1; i >= 1; i--) {
                                    int dealerPriceOff = hisDtoList.get(i - 1).getNewsPrice() - hisDtoList.get(i).getNewsPrice();
                                    if ((double) dealerPriceOff / hisDtoList.get(i - 1).getNewsPrice() > param.getPriceOffThreshold()) {
                                        dt = hisDtoList.get(i).getDate();
                                        priceOff = hisDtoList.get(i - 1).getNewsPrice() - hisDtoList.get(i).getNewsPrice();
                                        break;
                                    } else if (hisDtoList.get(i).getNewsPrice() > hisDtoList.get(i - 1).getNewsPrice()) {
                                        // 涨价了
                                        priceUp = true;
                                        break;
                                    }
                                }
                            }
                            if (priceUp
                                    || StringUtils.isEmpty(dt)
                                    || getDiffDays(dt, DateFormatUtils.format(new Date(), "yyyy-MM-dd")) > param.getDays()) {
                            } else if (StringUtils.isNotEmpty(dt) && priceOff > 0) {
                                CitySortHangqingDto.SpecPriceOffInfo specPriceOffInfo = new CitySortHangqingDto.SpecPriceOffInfo();
                                specPriceOffInfo.setSpecId(spec.getSpecId());
                                specPriceOffInfo.setDt(dt);
                                specPriceOffInfo.setPriceOff(priceOff);
                                specPriceOffInfoList.add(specPriceOffInfo);
                            }
                        }
                    });
                    if (!CollectionUtils.isEmpty(specPriceOffInfoList)) {
                        CitySortHangqingDto.PriceOffInfo priceOffInfo = new CitySortHangqingDto.PriceOffInfo();
                        String startDt = specPriceOffInfoList.stream().map(CitySortHangqingDto.SpecPriceOffInfo::getDt).sorted(Comparator.reverseOrder()).findFirst().get();
                        priceOffInfo.setStartDt(startDt);
                        priceOffInfo.setSpecPriceOffInfoList(specPriceOffInfoList);
                        CitySortHangqingDto.HangqingDto hangqingDto = buildPriceOffDtoList(dto, priceOffInfo);
                        finalResultList.add(hangqingDto);
                    }
                }
            } catch (Exception e) {
                log.error("降价车系计算错误，cityId={}，param={}", cityId, JsonUtil.toString(param), e);
            }
        });


        // 对结果进行排序
        resultList = resultList.stream().sorted(((o1, o2) -> {
            // 比较日期
            int diffDays = getDiffDays(o1.getPriceOffInfo().getStartDt(), o2.getPriceOffInfo().getStartDt());
            if (diffDays == 0) {
                if (Objects.nonNull(conditionDtoMap.get(o2.getSeriesId()))
                        && Objects.nonNull(conditionDtoMap.get(o1.getSeriesId()))) {
                    // 降幅如果相同，按照关注度排序
                    return conditionDtoMap.get(o2.getSeriesId()).getAttention() - conditionDtoMap.get(o1.getSeriesId()).getAttention();
                } else {
                    return 1;
                }
            } else {
                return diffDays > 0 ? 1 : -1;
            }
        })).toList();

        CitySortHangqingDto citySortHangqingDto = new CitySortHangqingDto();
        citySortHangqingDto.setDtoList(resultList);
        return citySortHangqingDto;
    }

    private CitySortHangqingDto.HangqingDto buildPriceOffDtoList(SeriesSortConditionDto sortDto,
                                                                 CitySortHangqingDto.PriceOffInfo priceOffInfo) {
        CitySortHangqingDto.HangqingDto hangqingDto = new CitySortHangqingDto.HangqingDto();
        hangqingDto.setSeriesId(sortDto.getSeriesId());
        hangqingDto.setBrandId(sortDto.getBrandId());
        hangqingDto.setLevelId(sortDto.getLevelId());
        hangqingDto.setMinPrice(sortDto.getSeriesMinPrice());
        hangqingDto.setMaxPrice(sortDto.getSeriesMaxPrice());
        hangqingDto.setEnergyType(sortDto.getEnergytype());
        hangqingDto.setFuelTypeDetail(sortDto.getFuelTypes());
        hangqingDto.setPriceOffInfo(priceOffInfo);
        return hangqingDto;
    }


    private Integer getDiffDays(String startDt, String endDt) {
        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date star = dft.parse(startDt); // 开始时间
            Date endDay = dft.parse(endDt); // 结束时间
            Long starTime = star.getTime();
            Long endTime = endDay.getTime();
            long num = endTime - starTime; // 时间戳相差的毫秒数
            return (int) (num / (24 * 60 * 60 * 1000));//除以一天的毫秒数
        } catch (ParseException e) {
            log.error("日期转换异常 startDt={}, endDt={}", startDt, endDt);
        }
        return null;
    }


    private CitySortHangqingDto getListByPriceHisSort(List<SeriesSortConditionDto> sortDtoList,
                                                      int cityId) {
        try {
            List<SeriesSortConditionDto> dtoList = new ArrayList<>(sortDtoList);
            Map<Integer, SeriesSortConditionDto> conditionDtoMap = dtoList.stream()
                    .collect(Collectors.toMap(SeriesSortConditionDto::getSeriesId, e -> e));

            List<CitySortHangqingDto.HangqingDto> hangqingDtoList = new ArrayList<>();
            List<CitySortHangqingDto.HangqingDto> finalHangqingDtoList = hangqingDtoList;
            dtoList.forEach(series -> {
                AtomicReference<List<CitySortHangqingDto.SpecPriceHisInfo>> specPriceHisInfoList =
                        new AtomicReference<>(new ArrayList<>());
                if (!CollectionUtils.isEmpty(series.getSpecPriceHisList())) {
                    series.getSpecPriceHisList().forEach(spec -> {
                        try {
                            CitySortHangqingDto.SpecPriceHisInfo hisInfo = getSpecPriceHisInfo(spec);
                            if (Objects.nonNull(hisInfo)) {
                                specPriceHisInfoList.get().add(hisInfo);
                            }
                        } catch (Exception e) {
                        }
                    });
                }
                if (!specPriceHisInfoList.get().isEmpty()) {
                    finalHangqingDtoList.add(buildPriceHisDto(series, specPriceHisInfoList.get()));
                }
            });
            // 按照最近降价日期排序,相同则按关注度排序
            hangqingDtoList = hangqingDtoList.stream().sorted((o1, o2) -> {
                if (o1.getPriceHisInfo().getLastDt().equals(o2.getPriceHisInfo().getLastDt())) {
                    return conditionDtoMap.get(o2.getSeriesId()).getAttention()
                            - conditionDtoMap.get(o1.getSeriesId()).getAttention();
                } else {
                    return o2.getPriceHisInfo().getLastDt().compareTo(o1.getPriceHisInfo().getLastDt());
                }
            }).toList();
            return new CitySortHangqingDto(hangqingDtoList);
        } catch (Exception ex) {
            log.error("getListByPriceHisSort异常-ex:", ex);
        }
        return null;
    }

    private CitySortHangqingDto.SpecPriceHisInfo getSpecPriceHisInfo(SeriesSortConditionDto.SpecCityPriceHis spec) {

        List<SeriesSortConditionDto.SpecCityPriceHisDto> priceHisResultList = spec.getDtoList().stream()
                .filter(e -> e.getNewsPrice() > 0)
                .toList();
        if (priceHisResultList.size() >= 2) {
            String firstDate = priceHisResultList.get(0).getNewsPrice() > 0
                    ? priceHisResultList.get(0).getDate()
                    : priceHisResultList.get(1).getDate();
            String lastDate = priceHisResultList.get(priceHisResultList.size() - 1).getDate();

            // 第一天的价格
            int firstDayPrice = priceHisResultList.get(0).getNewsPrice();
            // 最后一天的价格
            int lastDayPrice = priceHisResultList.get(priceHisResultList.size() - 1).getNewsPrice();
            // 最小价格
            int minPrice = priceHisResultList.stream()
                    .map(SeriesSortConditionDto.SpecCityPriceHisDto::getNewsPrice)
                    .filter(e -> e > 0)
                    .min(Integer::compare)
                    .orElse(0);
            if ((lastDayPrice == minPrice && minPrice > 0 && priceHisResultList.size() > 2)
                    || (firstDayPrice > lastDayPrice && priceHisResultList.size() == 3)) {
                // 至少降价一次标签
                boolean atLeastPriceOffOnce = false;
                // 获取最近一次降价的日期
                String dt = priceHisResultList.get(0).getDate();
                for (int i = priceHisResultList.size() - 1; i >= 1; i--) {
                    if (priceHisResultList.get(i).getNewsPrice() < priceHisResultList.get(i - 1).getNewsPrice()) {
                        dt = priceHisResultList.get(i).getDate();
                        atLeastPriceOffOnce = true;
                        break;
                    }
                }
                if (atLeastPriceOffOnce) {
                    CitySortHangqingDto.SpecPriceHisInfo specPriceHisInfo = new CitySortHangqingDto.SpecPriceHisInfo();
                    specPriceHisInfo.setSpecId(spec.getSpecId());
                    Integer diffDays = getDiffDays(firstDate, lastDate);
                    if (Objects.nonNull(diffDays) && diffDays >= 180) {
                        specPriceHisInfo.setPriceHisTag("180天新低");
                    } else {
                        specPriceHisInfo.setPriceHisTag("历史新低");
                    }
                    specPriceHisInfo.setLastDt(dt);
                    return specPriceHisInfo;
                }
            } else {
                throw new RuntimeException("没有获取到历史新低数据");
            }
        }
        return null;
    }

    private CitySortHangqingDto.HangqingDto buildPriceHisDto(SeriesSortConditionDto sortDto,
                                                             List<CitySortHangqingDto.SpecPriceHisInfo> specPriceHisInfoList) {
        CitySortHangqingDto.HangqingDto hangqingDto = new CitySortHangqingDto.HangqingDto();
        hangqingDto.setSeriesId(sortDto.getSeriesId());
        hangqingDto.setBrandId(sortDto.getBrandId());
        hangqingDto.setLevelId(sortDto.getLevelId());
        hangqingDto.setMinPrice(sortDto.getSeriesMinPrice());
        hangqingDto.setMaxPrice(sortDto.getSeriesMaxPrice());
        hangqingDto.setEnergyType(sortDto.getEnergytype());
        hangqingDto.setFuelTypeDetail(sortDto.getFuelTypes());
        String seriesLastDt = DateFormatUtils.format(specPriceHisInfoList.stream()
                .map(e -> DateUtil.parse(e.getLastDt(), "yyyy-MM-dd"))
                .max(Date::compareTo)
                .orElse(null), "yyyy-MM-dd");
        CitySortHangqingDto.PriceHisInfo priceHisInfo = new CitySortHangqingDto.PriceHisInfo();
        priceHisInfo.setLastDt(seriesLastDt);
        priceHisInfo.setSpecPriceHisInfoList(specPriceHisInfoList);
        priceHisInfo.setSpecPriceHisInfoList(specPriceHisInfoList);
        hangqingDto.setPriceHisInfo(priceHisInfo);
        return hangqingDto;
    }

    /**
     *
     * @description 获取本地行情车系列表数据
     * @author zzli
     * @param cityId 城市id
     * @param pageIndex 页数
     * @param pageSize 每页多少条
     * @param filterSeriesId 过滤的车系
     * @param energyType 能源类型：0不限,1燃油车,4纯电,5插电,6增程,456新能源
     * @param sortId 近期降价=5
     * @param carType 0全部 1乘用车 2商用车
     */
    public BasePageModel<CitySortHangqingDto.HangqingDto> getSeriesDataList(int cityId,
                                                                            int pageIndex,
                                                                            int pageSize,
                                                                            List<Integer>filterSeriesId,
                                                                            int energyType,
                                                                            int sortId,
                                                                            int carType) {
        try {
            CitySortHangqingDto citySortHangqingDto = get(cityId, sortId);
            if (Objects.nonNull(citySortHangqingDto) && !CollectionUtils.isEmpty(citySortHangqingDto.getDtoList())) {
                // 车系过滤
                if (!CollectionUtils.isEmpty(filterSeriesId)) {
                    citySortHangqingDto.getDtoList().removeIf(x -> filterSeriesId.contains(x.getSeriesId()));
                }
                List<CitySortHangqingDto.HangqingDto> hangqingDtos = citySortHangqingDto.getDtoList().stream()
                        .filter(e -> {
                            if (energyType != 0) {
                                if (energyType == 1) {
                                    return e.getEnergyType() == 0;
                                } else {
                                    List<String> energyTypeList = Arrays.asList(String.valueOf(energyType).split(""));
                                    return CollectionUtils.containsAny(energyTypeList,
                                            Arrays.asList(e.getFuelTypeDetail().split(",")));
                                }
                            }
                            return true;
                        }).filter(e -> {
                            if (carType > 0) {
                                if (carType == 1) {
                                    return !Level.isCVLevel(e.getLevelId());
                                } else if (carType == 2) {
                                    return Level.isCVLevel(e.getLevelId());
                                }
                            }
                            return true;
                        }).toList();
                return new BasePageModel<>(pageIndex, pageSize, hangqingDtos);
            }
        } catch (Exception e) {
            log.error("获取本地行情车系列表数据error", e);
        }
        return new BasePageModel<>();
    }
}


