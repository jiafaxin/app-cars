package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carext.CarsHangqingPageGetResponse;
import autohome.rpc.car.app_cars.v1.carext.CarsHangqingPageGetResponse.Result.Pvitem;
import autohome.rpc.car.app_cars.v1.carext.CarsHangqingSearchOptionsRequest;
import autohome.rpc.car.app_cars.v1.carext.CarsHangqingSearchOptionsResponse;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.service.components.car.SeriesAttentionComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesSpecComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesAttentionDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesSpecDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.hangqing.CitySortHangqingComponent;
import com.autohome.app.cars.service.components.hangqing.dtos.CitySortHangqingDto;
import com.autohome.app.cars.service.services.dtos.HangqingHeadConfig;
import com.autohome.app.cars.service.services.dtos.HangqingSearchOptionsGroupDto;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/6/25
 */
@Service
@Slf4j
public class CarsHangqingService {

    @Autowired
    private CitySortHangqingComponent citySortHangqingComponent;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SeriesAttentionComponent seriesAttentionComponent;

    @Autowired
    private SeriesSpecComponent seriesSpecComponent;

    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Value("${hangqingsearch_option:[]}")
    String hangqingsearch_option;

    @Value("${hangqingheadconfig:}")
    String hangqingheadconfig;

    @Value("${hangqingsearch_option_11650:[]}")
    private String hangqingsearch_option_11650;


    public CompletableFuture<CarsHangqingPageGetResponse.Result.Builder> getResult(int cityId,
                                                                                   int pageIndex,
                                                                                   int pageSize,
                                                                                   String brand,
                                                                                   String levelId,
                                                                                   String price,
                                                                                   String energyType,
                                                                                   int sortId) {
        CarsHangqingPageGetResponse.Result.Builder resultBuilder = CarsHangqingPageGetResponse.Result.newBuilder();
        CarsHangqingPageGetResponse.Result.PageInfo.Builder pageInfoDefault =
                CarsHangqingPageGetResponse.Result.PageInfo.newBuilder();
        pageInfoDefault.setPageindex(pageIndex);
        resultBuilder.setPageinfo(pageInfoDefault);

        CitySortHangqingDto citySortHangqingDto = citySortHangqingComponent.get(cityId, sortId);
        if (Objects.isNull(citySortHangqingDto)) {
            return CompletableFuture.completedFuture(resultBuilder);
        }

        try {
            // 预处理请求参数中的信息
            // 品牌
            List<Integer> brandIdList = StringUtils.isBlank(brand)
                    ? Collections.emptyList()
                    : Arrays.stream(brand.split(",")).map(Integer::valueOf).toList();
            // 级别
            List<Integer> levelIdList = StringUtils.isBlank(levelId)
                    ? Collections.emptyList()
                    : Arrays.stream(levelId.split(",")).map(Integer::valueOf).toList();
            // 价格
            List<Pair<Integer, Integer>> minMaxPricePairList = new ArrayList<>();
            if (StringUtils.isNotEmpty(price)) {
                String[] priceStrPairs = price.split(",");
                Arrays.stream(priceStrPairs).forEach(priceStr -> {
                    String[] priceStrArr = priceStr.split("-");
                    Integer minPrice = Integer.parseInt(priceStrArr[0]);
                    Integer maxPrice = Integer.parseInt(priceStrArr[1]);
                    minMaxPricePairList.add(Pair.of(minPrice, maxPrice));
                });
            }
            // 能源类型
            List<String> energyTypeList = StringUtils.isBlank(energyType)
                    ? Collections.emptyList()
                    : Arrays.stream(energyType.split(","))
                    .map(String::valueOf)
                    .toList();

            // 过滤处理
            List<CitySortHangqingDto.HangqingDto> filterResultList = citySortHangqingDto.getDtoList().stream()
                    .filter(e -> {
                        // 品牌
                        if (CollectionUtils.isEmpty(brandIdList)) {
                            return true;
                        } else {
                            return brandIdList.contains(e.getBrandId());
                        }
                    })
                    .filter(e -> {
                        // 级别
                        if (CollectionUtils.isEmpty(levelIdList)) {
                            return true;
                        } else {
                            return levelIdList.contains(e.getLevelId());
                        }
                    })
                    .filter(e -> {
                        // 能源类型
                        if (!CollectionUtils.isEmpty(energyTypeList)) {
                            if (energyTypeList.contains("1")) {
                                // 燃油
                                return e.getEnergyType() == 0;
                            } else if (energyTypeList.contains("456")) {
                                // 新能源 4纯电, 5插电, 6增程
                                return e.getEnergyType() == 1;
                            } else {
                                // 具体新能源类型 4纯电, 5插电, 6增程  线上有错误数据 fueltypes=“2,4,7”但是isnewenergy=0 车系id:2608
                                List<String> fuelTypes = Arrays.asList(e.getFuelTypeDetail().split(","));
                                return e.getEnergyType() == 1 && CollectionUtils.containsAny(fuelTypes, energyTypeList);
                            }
                        } else {
                            // 全部
                            return true;
                        }
                    })
                    .filter(e -> {
                        // 价格
                        if (CollectionUtils.isEmpty(minMaxPricePairList)) {
                            // 全部
                            return true;
                        } else {
                            AtomicBoolean flag = new AtomicBoolean(false);
                            minMaxPricePairList.forEach(pricePair -> {
                                        flag.set(flag.get() || e.getMinPrice() < pricePair.getRight()
                                                && e.getMaxPrice() > pricePair.getLeft());
                                    }
                            );
                            return flag.get();
                        }
                    })
                    .toList();

            // 手动分页处理
            int start = (pageIndex - 1) * pageSize;
            int end = Math.min(start + pageSize, filterResultList.size());
            int totalCount = filterResultList.size();
            List<CitySortHangqingDto.HangqingDto> pageResult = new ArrayList<>();
            for (int i = start; i < filterResultList.size() && i < end; i++) {
                pageResult.add(filterResultList.get(i));
            }

            CarsHangqingPageGetResponse.Result.PageInfo.Builder pageInfo =
                    CarsHangqingPageGetResponse.Result.PageInfo.newBuilder();
            pageInfo.setPageindex(pageIndex);
            pageInfo.setPagecount((int) Math.ceil((double) totalCount / pageSize));
            pageInfo.setRowcount(totalCount);

            resultBuilder.setPageinfo(pageInfo);
            resultBuilder.addAllCardlist(getCardList(pageResult, cityId, sortId));

        } catch (Exception e) {
            log.error("获取车辆行情数据异常", e);
        }
        return CompletableFuture.completedFuture(resultBuilder);

    }

    /**
     * 拼装所有卡片
     *
     * @param hangqingList 行情解结果列表
     * @param cityId       城市id
     * @return 卡片列表
     */
    private List<CarsHangqingPageGetResponse.Result.CardList> getCardList(
            List<CitySortHangqingDto.HangqingDto> hangqingList,
            int cityId,
            int sortId) {
        switch (sortId) {
            case 1, 2, 3, 4:
                return getCardList30501(hangqingList, cityId, sortId);
            case 5:
                // todo
                return getCardList30502(hangqingList, cityId, sortId);
            case 6:
                // todo
                return getCardList30503(hangqingList, cityId, sortId);
            default:
                // todo
                return null;
        }
    }

    /**
     * 历史新低
     *
     * @param hangqingList
     * @param cityId
     * @return
     */
    private List<CarsHangqingPageGetResponse.Result.CardList> getCardList30503(
            List<CitySortHangqingDto.HangqingDto> hangqingList,
            int cityId,
            int sortId) {

        List<Integer> seriesIdList = hangqingList.stream().map(CitySortHangqingDto.HangqingDto::getSeriesId).toList();
        Map<Integer, CitySortHangqingDto.PriceHisInfo> seriesPirceHisMap = hangqingList.stream().collect(Collectors.toMap(CitySortHangqingDto.HangqingDto::getSeriesId, CitySortHangqingDto.HangqingDto::getPriceHisInfo));
        // 获取车系详情、车系下所有车型、车系下车型热度等信息
        List<CompletableFuture> preparationTasks = new ArrayList<>();
        AtomicReference<List<SeriesDetailDto>> SeriesDetailListRef = new AtomicReference<>();
        AtomicReference<List<SeriesSpecDto>> SeriesSpecListRef = new AtomicReference<>();
        AtomicReference<List<SeriesAttentionDto>> SeriesAttentionListRef = new AtomicReference<>();
        preparationTasks.add(seriesDetailComponent.getList(seriesIdList)
                .thenAccept(SeriesDetailListRef::set).exceptionally(e -> null));
        preparationTasks.add(seriesSpecComponent.get(seriesIdList)
                .thenAccept(SeriesSpecListRef::set).exceptionally(e -> null));
        preparationTasks.add(seriesAttentionComponent.getList(seriesIdList)
                .thenAccept(SeriesAttentionListRef::set).exceptionally(e -> null));
        CompletableFuture.allOf(preparationTasks.toArray(new CompletableFuture[0])).join();
        if (Objects.isNull(SeriesSpecListRef.get())) {
            return Collections.emptyList();
        }

        Map<Integer, SeriesDetailDto> seriesDetailDtoMap = SeriesDetailListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity()));
        Map<Integer, SeriesSpecDto> seriesSpecDtoMap = SeriesSpecListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesSpecDto::getSeriesId, Function.identity()));
        Map<Integer, SeriesAttentionDto> seriesAttentionDtoMap = SeriesAttentionListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesAttentionDto::getSeriesId, Function.identity()));

        // 获取车系下的 车型详情、车型在对应城市的询价
        List<CompletableFuture> seriesSpecTasks = new ArrayList<>();
        ConcurrentHashMap<Integer, SpecDetailDto> specDetailMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecCityAskPriceDto> specCityAskPriceMap = new ConcurrentHashMap<>();
        seriesSpecDtoMap.values().forEach(seriesSpecDto -> {
            List<Integer> specIdList = seriesSpecDto.getItems().stream()
                    .filter(e -> e.getState() == 20 || e.getState() == 30)
                    .map(SeriesSpecDto.Item::getId)
                    .toList();
            seriesSpecTasks.add(specDetailComponent.getList(specIdList)
                    .thenAccept(r -> r.forEach(spec -> specDetailMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
            seriesSpecTasks.add(specCityAskPriceComponent.getListAsync(specIdList, cityId)
                    .thenAccept(r -> r.forEach(spec -> specCityAskPriceMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
        });
        CompletableFuture.allOf(seriesSpecTasks.toArray(new CompletableFuture[0])).join();

        // 组装车系车系行情结果
        List<CarsHangqingPageGetResponse.Result.CardList> cardListList = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(1);
        seriesIdList.forEach(seriesId -> {
            CarsHangqingPageGetResponse.Result.CardList.Builder cardList =
                    CarsHangqingPageGetResponse.Result.CardList.newBuilder();
            CarsHangqingPageGetResponse.Result.Card30503.Builder card30503 =
                    CarsHangqingPageGetResponse.Result.Card30503.newBuilder();

            CarsHangqingPageGetResponse.Result.Card30503.SeriesInfo.Builder seriesInfo =
                    CarsHangqingPageGetResponse.Result.Card30503.SeriesInfo.newBuilder();
            CitySortHangqingDto.PriceHisInfo priceHisInfo = seriesPirceHisMap.get(seriesId);
            Map<Integer, CitySortHangqingDto.SpecPriceHisInfo> specPriceHisMap = priceHisInfo.getSpecPriceHisInfoList().stream().collect(Collectors.toMap(CitySortHangqingDto.SpecPriceHisInfo::getSpecId, Function.identity()));
            SeriesDetailDto seriesDetailDto = seriesDetailDtoMap.get(seriesId);
            SeriesSpecDto seriesSpec = seriesSpecDtoMap.get(seriesId);
            long onSaleCount = Objects.nonNull(seriesSpec)
                    ? seriesSpec.getItems().stream().filter(e -> e.getState() == 20 || e.getState() == 30).count()
                    : 0;
            if (Objects.nonNull(seriesDetailDto)) {
                // 组装车系信息
                seriesInfo.setId(seriesDetailDto.getId());
                seriesInfo.setLogo(seriesDetailDto.getBrandLogo());
                seriesInfo.setPicurl(seriesDetailDto.getPngLogo());
                seriesInfo.setSeriesname(seriesDetailDto.getName());
                seriesInfo.setPrice(seriesDetailDto.getPrice());
                seriesInfo.setCount(String.valueOf(onSaleCount));
                Pvitem.Builder seriesPvBuilder = Pvitem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                        .putArgvs("specid", String.valueOf(0))
                        .putArgvs("tag", Integer.toString(sortId))
                        .putArgvs("index",index.get()+"")
                        .setClick(Pvitem.Click.newBuilder().setEventid("carmarketchannel_card_click"))
                        .setShow(Pvitem.Show.newBuilder().setEventid("carmarketchannel_card_show"));
                seriesInfo.setPvitem(seriesPvBuilder.build());

                List<CarsHangqingPageGetResponse.Result.Card30503.SpecInfo> specInfoList = new ArrayList<>();

                // 按照车系下车型关注度获取车型列表
                SeriesAttentionDto seriesAttentionDto = seriesAttentionDtoMap.get(seriesDetailDto.getId());
                Map<Integer, Integer> specAttMap;
                if (Objects.nonNull(seriesAttentionDto)) {
                    specAttMap = seriesAttentionDto.getSpecAttentions().stream()
                            .collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                                    SeriesAttentionDto.SpecAttention::getAttention));
                } else {
                    specAttMap = new HashMap<>();
                }

                // 取所有在售车型，先看其是否含有历史新低/180天新低标签
                List<Integer> specIdList = seriesSpecDtoMap.get(seriesDetailDto.getId()).getItems().stream()
                        .filter(e -> e.getState() == 20 || e.getState() == 30)
                        .sorted((o1, o2) -> {
                            boolean o1HasPriceHis = Objects.nonNull(specPriceHisMap.get(o1.getId()));
                            boolean o2HasPriceHis = Objects.nonNull(specPriceHisMap.get(o2.getId()));
                            if (o1HasPriceHis && o2HasPriceHis) {
                                //两个车型都含有历史新低/180天新降两个标签其中之一，则根据最新一次降价日期比较
                                String o1_dt = specPriceHisMap.get(o1.getId()).getLastDt();
                                String o2_dt = specPriceHisMap.get(o2.getId()).getLastDt();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
                                Date o1_date = null;
                                Date o2_date = null;
                                try {
                                    o1_date = simpleDateFormat.parse(o1_dt);
                                    o2_date = simpleDateFormat.parse(o2_dt);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                int compare_result = o2_date.compareTo(o1_date);
                                //降价日期不相同，则日期近的排前边
                                if (compare_result != 0) {
                                    return compare_result;
                                }
                                //降价日期相同，再按照关注度进行排序
                                if (Objects.nonNull(specAttMap.get(o1.getId()))
                                        && Objects.nonNull(specAttMap.get(o2.getId()))) {
                                    return specAttMap.get(o2.getId()) - specAttMap.get(o1.getId());
                                } else if (Objects.nonNull(specAttMap.get(o1.getId()))) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            } else if (o1HasPriceHis) {
                                return -1;
                            } else if (o2HasPriceHis) {
                                return 1;
                            } else {
                                //剩余无"历史新低/180天新低"标签的车型按照关注度倒排
                                if (Objects.nonNull(specAttMap.get(o1.getId()))
                                        && Objects.nonNull(specAttMap.get(o2.getId()))) {
                                    return specAttMap.get(o2.getId()) - specAttMap.get(o1.getId());
                                } else if (Objects.nonNull(specAttMap.get(o1.getId()))) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        })
                        .map(SeriesSpecDto.Item::getId)
                        .toList();

                specIdList.forEach(specId -> {
                    if (StringUtils.isEmpty(seriesInfo.getJumpurl()))
                    //车型id应该是有历史新低且关注度最高的
                    seriesInfo.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                            seriesDetailDto.getBrandId(), seriesDetailDto.getId(), specId,
                            UrlUtil.encode(seriesDetailDto.getName()), 8, UrlUtil.encode("本地报价")));

                    SpecDetailDto specDetailDto = specDetailMap.get(specId);
                    SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceMap.get(specId);
                    if (Objects.nonNull(specDetailDto)) {
                        CarsHangqingPageGetResponse.Result.Card30503.SpecInfo.Builder specBuilder =
                                CarsHangqingPageGetResponse.Result.Card30503.SpecInfo.newBuilder();
                        specBuilder.setSpecname(specDetailDto.getSpecName());
                        specBuilder.setGuidprice(getPriceInfo(specDetailDto.getMinPrice()));
                        if (Objects.isNull(specCityAskPriceDto) || specCityAskPriceDto.getMinPrice() == 0) {
                            specBuilder.setPrice("");
                            specBuilder.setPricedown("");
                        } else {
                            int difPrice = specCityAskPriceDto.getMinPrice() - specDetailDto.getMinPrice();
                            specBuilder.setPricedecrease(difPrice < 0);
                            specBuilder.setPrice(getPriceInfo(specCityAskPriceDto.getMinPrice()));
                            specBuilder.setPricedown(getPriceInfo(Math.abs(difPrice)));
                            CitySortHangqingDto.SpecPriceHisInfo specPriceHisInfo = specPriceHisMap.get(specId);
                            specBuilder.setPricehistorytag(specPriceHisInfo == null ? "" : specPriceHisInfo.getPriceHisTag());
                        }
                        specBuilder.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                                seriesDetailDto.getBrandId(), seriesDetailDto.getId(), specDetailDto.getSpecId(),
                                UrlUtil.encode(seriesDetailDto.getName()), 8, UrlUtil.encode("本地报价")));
                        Pvitem.Builder specPvBuilder =
                                Pvitem.newBuilder()
                                        .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                                        .putArgvs("specid", String.valueOf(specId))
                                        .putArgvs("tag", Integer.toString(sortId))
                                        .putArgvs("index",index.get()+"")
                                        .setClick(Pvitem.Click.newBuilder().setEventid("carmarketchannel_card_click"));
                        specBuilder.setPvitem(specPvBuilder.build());
                        specInfoList.add(specBuilder.build());
                    }
                });

                card30503.setSeriesinfo(seriesInfo);
                card30503.setSpeccount((int) onSaleCount);
                card30503.addAllSpeclist(specInfoList);
                Pvitem.Builder cardPvBuilder = Pvitem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                        .putArgvs("specid", String.valueOf(0))
                        .setClick(Pvitem.Click.newBuilder().setEventid("carmarketchannel_more_click"));
                card30503.setPvitem(cardPvBuilder.build());

                cardList.setType(30503);
                cardList.setCard30503(card30503);
                cardListList.add(cardList.build());
                index.getAndIncrement();
            }
        });
        return cardListList;
    }

    /**
     * 近期降价
     *
     * @param hangqingList
     * @param cityId
     * @return
     */
    private List<CarsHangqingPageGetResponse.Result.CardList> getCardList30502(
            List<CitySortHangqingDto.HangqingDto> hangqingList,
            int cityId,
            int sortId) {
        List<Integer> seriesIdList = hangqingList.stream().map(CitySortHangqingDto.HangqingDto::getSeriesId).toList();
        Map<Integer, CitySortHangqingDto.PriceOffInfo> seriesPriceOffMap = hangqingList.stream().collect(Collectors.toMap(CitySortHangqingDto.HangqingDto::getSeriesId, CitySortHangqingDto.HangqingDto::getPriceOffInfo));
        // 获取车系详情、车系下所有车型、车系下车型热度等信息
        List<CompletableFuture> preparationTasks = new ArrayList<>();
        AtomicReference<List<SeriesDetailDto>> SeriesDetailListRef = new AtomicReference<>();
        AtomicReference<List<SeriesSpecDto>> SeriesSpecListRef = new AtomicReference<>();
        AtomicReference<List<SeriesAttentionDto>> SeriesAttentionListRef = new AtomicReference<>();
        preparationTasks.add(seriesDetailComponent.getList(seriesIdList)
                .thenAccept(SeriesDetailListRef::set).exceptionally(e -> null));
        preparationTasks.add(seriesSpecComponent.get(seriesIdList)
                .thenAccept(SeriesSpecListRef::set).exceptionally(e -> null));
        preparationTasks.add(seriesAttentionComponent.getList(seriesIdList)
                .thenAccept(SeriesAttentionListRef::set).exceptionally(e -> null));
        CompletableFuture.allOf(preparationTasks.toArray(new CompletableFuture[0])).join();
        if (Objects.isNull(SeriesSpecListRef.get())) {
            return Collections.emptyList();
        }

        Map<Integer, SeriesDetailDto> seriesDetailDtoMap = SeriesDetailListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity()));
        Map<Integer, SeriesSpecDto> seriesSpecDtoMap = SeriesSpecListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesSpecDto::getSeriesId, Function.identity()));
        Map<Integer, SeriesAttentionDto> seriesAttentionDtoMap = SeriesAttentionListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesAttentionDto::getSeriesId, Function.identity()));

        // 获取车系下的 车型详情、车型在对应城市的询价
        List<CompletableFuture> seriesSpecTasks = new ArrayList<>();
        ConcurrentHashMap<Integer, SpecDetailDto> specDetailMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecCityAskPriceDto> specCityAskPriceMap = new ConcurrentHashMap<>();
        seriesSpecDtoMap.values().forEach(seriesSpecDto -> {
            List<Integer> specIdList = seriesSpecDto.getItems().stream()
                    .filter(e -> e.getState() == 20 || e.getState() == 30)
                    .map(SeriesSpecDto.Item::getId)
                    .toList();
            seriesSpecTasks.add(specDetailComponent.getList(specIdList)
                    .thenAccept(r -> r.forEach(spec -> specDetailMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
            seriesSpecTasks.add(specCityAskPriceComponent.getListAsync(specIdList, cityId)
                    .thenAccept(r -> r.forEach(spec -> specCityAskPriceMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
        });
        CompletableFuture.allOf(seriesSpecTasks.toArray(new CompletableFuture[0])).join();

        // 组装车系车系行情结果
        List<CarsHangqingPageGetResponse.Result.CardList> cardListList = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(1);
        seriesIdList.forEach(seriesId -> {
            CarsHangqingPageGetResponse.Result.CardList.Builder cardList =
                    CarsHangqingPageGetResponse.Result.CardList.newBuilder();
            CarsHangqingPageGetResponse.Result.Card30502.Builder card30502 =
                    CarsHangqingPageGetResponse.Result.Card30502.newBuilder();

            CarsHangqingPageGetResponse.Result.Card30502.SeriesInfo.Builder seriesInfo =
                    CarsHangqingPageGetResponse.Result.Card30502.SeriesInfo.newBuilder();
            CitySortHangqingDto.PriceOffInfo priceOffInfo = seriesPriceOffMap.get(seriesId);//近期降价
            Map<Integer, CitySortHangqingDto.SpecPriceOffInfo> specPriceOffMap = priceOffInfo.getSpecPriceOffInfoList().stream().collect(Collectors.toMap(CitySortHangqingDto.SpecPriceOffInfo::getSpecId, Function.identity()));
            SeriesDetailDto seriesDetailDto = seriesDetailDtoMap.get(seriesId);
            SeriesSpecDto seriesSpec = seriesSpecDtoMap.get(seriesId);
            long onSaleCount = Objects.nonNull(seriesSpec)
                    ? seriesSpec.getItems().stream().filter(e -> e.getState() == 20 || e.getState() == 30).count()
                    : 0;
            if (Objects.nonNull(seriesDetailDto)) {
                // 组装车系信息
                seriesInfo.setId(seriesDetailDto.getId());
                seriesInfo.setLogo(seriesDetailDto.getBrandLogo());
                seriesInfo.setPicurl(seriesDetailDto.getPngLogo());
                seriesInfo.setSeriesname(seriesDetailDto.getName());
                seriesInfo.setPrice(seriesDetailDto.getPrice());
                seriesInfo.setCount(String.valueOf(onSaleCount));
                Pvitem.Builder seriesPvBuilder = Pvitem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                        .putArgvs("specid", String.valueOf(0))
                        .putArgvs("tag", Integer.toString(sortId))
                        .putArgvs("index",index.get()+"")
                        .setClick(Pvitem.Click.newBuilder().setEventid("carmarketchannel_card_click"))
                        .setShow(Pvitem.Show.newBuilder().setEventid("carmarketchannel_card_show"));
                seriesInfo.setPvitem(seriesPvBuilder.build());

                List<CarsHangqingPageGetResponse.Result.Card30502.SpecInfo> specInfoList = new ArrayList<>();

                // 按照车系下车型关注度获取车型列表
                SeriesAttentionDto seriesAttentionDto = seriesAttentionDtoMap.get(seriesDetailDto.getId());
                Map<Integer, Integer> specAttMap;
                if (Objects.nonNull(seriesAttentionDto)) {
                    specAttMap = seriesAttentionDto.getSpecAttentions().stream()
                            .collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                                    SeriesAttentionDto.SpecAttention::getAttention));
                } else {
                    specAttMap = new HashMap<>();
                }

                //取所有在售车型，先根据降价日期倒排，同一个日期再按照关注度倒排
                List<Integer> specIdList = seriesSpecDtoMap.get(seriesDetailDto.getId()).getItems().stream()
                        .filter(e -> e.getState() == 20 || e.getState() == 30)
                        .sorted((o1, o2) -> {
                            if (Objects.nonNull(specPriceOffMap.get(o1.getId()))
                                    && Objects.nonNull(specPriceOffMap.get(o2.getId()))) {
                                String o1_dt = specPriceOffMap.get(o1.getId()).getDt();
                                String o2_dt = specPriceOffMap.get(o2.getId()).getDt();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
                                Date o1_date = null;
                                Date o2_date = null;
                                try {
                                    o1_date = simpleDateFormat.parse(o1_dt);
                                    o2_date = simpleDateFormat.parse(o2_dt);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                int compare_result = o2_date.compareTo(o1_date);
                                if (compare_result != 0) {
                                    return compare_result;//两个车型的降价日期不相同，直接返回
                                }
                                //两个车型的降价日期相同，再根据关注度进行倒排
                                if (Objects.nonNull(specAttMap.get(o1.getId()))
                                        && Objects.nonNull(specAttMap.get(o2.getId()))) {
                                    return specAttMap.get(o2.getId()) - specAttMap.get(o1.getId());
                                } else if (Objects.nonNull(specAttMap.get(o1.getId()))) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            } else if (Objects.nonNull(specPriceOffMap.get(o1.getId()))) {
                                return -1;
                            } else if (Objects.nonNull(specPriceOffMap.get(o2.getId()))){
                                return 1;
                            }else{
                                //无降价的车型仍按照关注度倒排
                                if (Objects.nonNull(specAttMap.get(o1.getId()))
                                        && Objects.nonNull(specAttMap.get(o2.getId()))) {
                                    return specAttMap.get(o2.getId()) - specAttMap.get(o1.getId());
                                } else if (Objects.nonNull(specAttMap.get(o1.getId()))) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        })
                        .map(SeriesSpecDto.Item::getId)
                        .toList();


                specIdList.forEach(specId -> {
                    if (StringUtils.isEmpty(seriesInfo.getJumpurl()))
                        //车型id应该是降价日期最近且关注度最高的
                        seriesInfo.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                                seriesDetailDto.getBrandId(), seriesDetailDto.getId(), specId,
                                UrlUtil.encode(seriesDetailDto.getName()), 8, UrlUtil.encode("本地报价")));

                    SpecDetailDto specDetailDto = specDetailMap.get(specId);
                    SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceMap.get(specId);
                    if (Objects.nonNull(specDetailDto)) {
                        CarsHangqingPageGetResponse.Result.Card30502.SpecInfo.Builder specBuilder =
                                CarsHangqingPageGetResponse.Result.Card30502.SpecInfo.newBuilder();
                        specBuilder.setSpecname(specDetailDto.getSpecName());
                        specBuilder.setGuidprice(getPriceInfo(specDetailDto.getMinPrice()));
                        if (Objects.isNull(specCityAskPriceDto) || specCityAskPriceDto.getMinPrice() == 0) {
                            specBuilder.setPrice("");
                            specBuilder.setPricedown("");
                        } else {
                            int difPrice = specCityAskPriceDto.getMinPrice() - specDetailDto.getMinPrice();
                            specBuilder.setPricedecrease(difPrice < 0);
                            specBuilder.setPrice(getPriceInfo(specCityAskPriceDto.getMinPrice()));
                            specBuilder.setPricedown(getPriceInfo(Math.abs(difPrice)));
                            CitySortHangqingDto.SpecPriceOffInfo specPriceOffInfo = specPriceOffMap.get(specId);
                            specBuilder.setPricedowntag(null == specPriceOffInfo ? "" : formatDate(specPriceOffInfo.getDt()) + "新降" + getPriceWithOneDecimal(specPriceOffInfo.getPriceOff()));
                        }
                        specBuilder.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                                seriesDetailDto.getBrandId(), seriesDetailDto.getId(), specDetailDto.getSpecId(),
                                UrlUtil.encode(seriesDetailDto.getName()), 8, UrlUtil.encode("本地报价")));
                        Pvitem.Builder specPvBuilder =
                                Pvitem.newBuilder()
                                        .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                                        .putArgvs("specid", String.valueOf(specId))
                                        .putArgvs("tag", Integer.toString(sortId))
                                        .putArgvs("index",index.get()+"")
                                        .setClick(Pvitem.Click.newBuilder().setEventid("carmarketchannel_card_click"));
                        specBuilder.setPvitem(specPvBuilder.build());
                        specInfoList.add(specBuilder.build());
                    }
                });

                card30502.setSeriesinfo(seriesInfo);
                card30502.setSpeccount((int) onSaleCount);
                card30502.addAllSpeclist(specInfoList);
                Pvitem.Builder cardPvBuilder = Pvitem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                        .putArgvs("specid", String.valueOf(0))
                        .setClick(Pvitem.Click.newBuilder().setEventid("carmarketchannel_more_click"));
                card30502.setPvitem(cardPvBuilder.build());

                cardList.setType(30502);
                cardList.setCard30502(card30502);
                cardListList.add(cardList.build());
                index.getAndIncrement();
            }
        });
        return cardListList;
    }

    private List<CarsHangqingPageGetResponse.Result.CardList> getCardList30501(
            List<CitySortHangqingDto.HangqingDto> hangqingList,
            int cityId,
            int sortId) {
        List<Integer> seriesIdList = hangqingList.stream().map(CitySortHangqingDto.HangqingDto::getSeriesId).toList();

        // 获取车系详情、车系下所有车型、车系下车型热度等信息
        List<CompletableFuture> preparationTasks = new ArrayList<>();
        AtomicReference<List<SeriesDetailDto>> SeriesDetailListRef = new AtomicReference<>();
        AtomicReference<List<SeriesSpecDto>> SeriesSpecListRef = new AtomicReference<>();
        AtomicReference<List<SeriesAttentionDto>> SeriesAttentionListRef = new AtomicReference<>();
        preparationTasks.add(seriesDetailComponent.getList(seriesIdList)
                .thenAccept(SeriesDetailListRef::set).exceptionally(e -> null));
        preparationTasks.add(seriesSpecComponent.get(seriesIdList)
                .thenAccept(SeriesSpecListRef::set).exceptionally(e -> null));
        preparationTasks.add(seriesAttentionComponent.getList(seriesIdList)
                .thenAccept(SeriesAttentionListRef::set).exceptionally(e -> null));
        CompletableFuture.allOf(preparationTasks.toArray(new CompletableFuture[0])).join();
        if (Objects.isNull(SeriesSpecListRef.get())) {
            return Collections.emptyList();
        }

        Map<Integer, SeriesDetailDto> seriesDetailDtoMap = SeriesDetailListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity()));
        Map<Integer, SeriesSpecDto> seriesSpecDtoMap = SeriesSpecListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesSpecDto::getSeriesId, Function.identity()));
        Map<Integer, SeriesAttentionDto> seriesAttentionDtoMap = SeriesAttentionListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesAttentionDto::getSeriesId, Function.identity()));

        // 获取车系下的 车型详情、车型在对应城市的询价
        List<CompletableFuture> seriesSpecTasks = new ArrayList<>();
        ConcurrentHashMap<Integer, SpecDetailDto> specDetailMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecCityAskPriceDto> specCityAskPriceMap = new ConcurrentHashMap<>();
        seriesSpecDtoMap.values().forEach(seriesSpecDto -> {
            List<Integer> specIdList = seriesSpecDto.getItems().stream()
                    .filter(e -> e.getState() == 20 || e.getState() == 30)
                    .map(SeriesSpecDto.Item::getId)
                    .toList();
            seriesSpecTasks.add(specDetailComponent.getList(specIdList)
                    .thenAccept(r -> r.forEach(spec -> specDetailMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
            seriesSpecTasks.add(specCityAskPriceComponent.getListAsync(specIdList, cityId)
                    .thenAccept(r -> r.forEach(spec -> specCityAskPriceMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
        });
        CompletableFuture.allOf(seriesSpecTasks.toArray(new CompletableFuture[0])).join();

        // 组装车系车系行情结果
        List<CarsHangqingPageGetResponse.Result.CardList> cardListList = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(1);
        seriesIdList.forEach(seriesId -> {
            CarsHangqingPageGetResponse.Result.CardList.Builder cardList =
                    CarsHangqingPageGetResponse.Result.CardList.newBuilder();
            CarsHangqingPageGetResponse.Result.Card30501.Builder card30501 =
                    CarsHangqingPageGetResponse.Result.Card30501.newBuilder();

            CarsHangqingPageGetResponse.Result.Card30501.SeriesInfo.Builder seriesInfo =
                    CarsHangqingPageGetResponse.Result.Card30501.SeriesInfo.newBuilder();
            SeriesDetailDto seriesDetailDto = seriesDetailDtoMap.get(seriesId);
            SeriesSpecDto seriesSpec = seriesSpecDtoMap.get(seriesId);
            long onSaleCount = Objects.nonNull(seriesSpec)
                    ? seriesSpec.getItems().stream().filter(e -> e.getState() == 20 || e.getState() == 30).count()
                    : 0;
            if (Objects.nonNull(seriesDetailDto)) {
                // 组装车系信息
                seriesInfo.setId(seriesDetailDto.getId());
                seriesInfo.setLogo(seriesDetailDto.getBrandLogo());
                seriesInfo.setPicurl(seriesDetailDto.getPngLogo());
                seriesInfo.setSeriesname(seriesDetailDto.getName());
                seriesInfo.setPrice(seriesDetailDto.getPrice());
                seriesInfo.setCount(String.valueOf(onSaleCount));
                seriesInfo.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                        seriesDetailDto.getBrandId(), seriesDetailDto.getId(), seriesDetailDto.getHotSpecId(),
                        UrlUtil.encode(seriesDetailDto.getName()), 8, UrlUtil.encode("本地报价")));
                Pvitem.Builder seriesPvBuilder = Pvitem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                        .putArgvs("specid", String.valueOf(0))
                        .putArgvs("tag", Integer.toString(sortId))
                        .putArgvs("index",index.get()+"")
                        .setClick(Pvitem.Click.newBuilder().setEventid("carmarketchannel_card_click"))
                        .setShow(Pvitem.Show.newBuilder().setEventid("carmarketchannel_card_show"));
                seriesInfo.setPvitem(seriesPvBuilder.build());

                List<CarsHangqingPageGetResponse.Result.Card30501.SpecInfo> specInfoList = new ArrayList<>();

                // 按照车系下车型关注度获取车型列表
                SeriesAttentionDto seriesAttentionDto = seriesAttentionDtoMap.get(seriesDetailDto.getId());
                Map<Integer, Integer> specAttMap;
                if (Objects.nonNull(seriesAttentionDto)) {
                    specAttMap = seriesAttentionDto.getSpecAttentions().stream()
                            .collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                                    SeriesAttentionDto.SpecAttention::getAttention));
                } else {
                    specAttMap = new HashMap<>();
                }
                // 取所有在售车型且按照关注度倒序
                List<Integer> specIdList = handleSpecSort(sortId, seriesSpecDtoMap, seriesDetailDto,
                        specCityAskPriceMap, specDetailMap, specAttMap);

                specIdList.forEach(specId -> {
                    SpecDetailDto specDetailDto = specDetailMap.get(specId);
                    SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceMap.get(specId);
                    if (Objects.nonNull(specDetailDto)) {
                        CarsHangqingPageGetResponse.Result.Card30501.SpecInfo.Builder specBuilder =
                                CarsHangqingPageGetResponse.Result.Card30501.SpecInfo.newBuilder();
                        specBuilder.setSpecname(specDetailDto.getSpecName());
                        specBuilder.setGuidprice(getPriceInfo(specDetailDto.getMinPrice()));
                        if (Objects.isNull(specCityAskPriceDto) || specCityAskPriceDto.getMinPrice() == 0) {
                            specBuilder.setPrice("");
                            specBuilder.setPricedown("");
                        } else {
                            int difPrice = specCityAskPriceDto.getMinPrice() - specDetailDto.getMinPrice();
                            specBuilder.setPricedecrease(difPrice < 0);
                            specBuilder.setPrice(getPriceInfo(specCityAskPriceDto.getMinPrice()));
                            specBuilder.setPricedown(getPriceInfo(Math.abs(difPrice)));
                        }
                        specBuilder.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                                seriesDetailDto.getBrandId(), seriesDetailDto.getId(), specDetailDto.getSpecId(),
                                UrlUtil.encode(seriesDetailDto.getName()), 8, UrlUtil.encode("本地报价")));
                        Pvitem.Builder specPvBuilder =
                                Pvitem.newBuilder()
                                        .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                                        .putArgvs("specid", String.valueOf(specId))
                                        .putArgvs("tag", Integer.toString(sortId))
                                        .putArgvs("index",index.get()+"")
                                        .setClick(Pvitem.Click.newBuilder().setEventid("carmarketchannel_card_click"));
                        specBuilder.setPvitem(specPvBuilder.build());
                        specInfoList.add(specBuilder.build());
                    }
                });

                card30501.setSeriesinfo(seriesInfo);
                card30501.setSpeccount((int) onSaleCount);
                card30501.addAllSpeclist(specInfoList);
                Pvitem.Builder cardPvBuilder = Pvitem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                        .putArgvs("specid", String.valueOf(0))
                        .setClick(Pvitem.Click.newBuilder().setEventid("carmarketchannel_more_click"));
                card30501.setPvitem(cardPvBuilder.build());

                cardList.setType(30501);
                cardList.setCard30501(card30501);
                cardListList.add(cardList.build());
                index.getAndIncrement();
            }
        });
        return cardListList;
    }

    @Data
    private static class SpecSortDto {
        int specId;
        double priceOff;
        int att;
    }

    private List<Integer> handleSpecSort(int sortId,
                                         Map<Integer, SeriesSpecDto> seriesSpecDtoMap,
                                         SeriesDetailDto seriesDetailDto,
                                         ConcurrentHashMap<Integer, SpecCityAskPriceDto> specCityAskPriceMap,
                                         ConcurrentHashMap<Integer, SpecDetailDto> specDetailMap,
                                         Map<Integer, Integer> specAttMap) {
        if (sortId == 1 || sortId == 2) {
            return seriesSpecDtoMap.get(seriesDetailDto.getId()).getItems().stream()
                    .filter(e -> e.getState() == 20 || e.getState() == 30)
                    .map(e -> {
                        SpecSortDto specSortDto = new SpecSortDto();
                        specSortDto.setSpecId(e.getId());
                        SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceMap.get(e.getId());
                        SpecDetailDto specDetailDto = specDetailMap.get(e.getId());
                        if (Objects.nonNull(specCityAskPriceDto) && Objects.nonNull(specDetailDto)) {
                            specSortDto.setPriceOff((double) (specDetailDto.getMinPrice() - specCityAskPriceDto.getMinPrice()) / specDetailDto.getMinPrice());
                        } else {
                            specSortDto.setPriceOff(0D);
                        }
                        if (Objects.nonNull(specAttMap.get(e.getId()))) {
                            specSortDto.setAtt(specAttMap.get(e.getId()));
                        } else {
                            specSortDto.setAtt(0);
                        }
                        return specSortDto;
                    })
                    .sorted((o1, o2) -> {
                        if (o1.getPriceOff() != o2.getPriceOff()) {
                            return o2.getPriceOff() - o1.getPriceOff() > 0 ? 1 : -1;
                        } else {
                            return o2.getAtt() - o1.getAtt();
                        }
                    })
                    .map(SpecSortDto::getSpecId)
                    .toList();
        } else {
            return seriesSpecDtoMap.get(seriesDetailDto.getId()).getItems().stream()
                    .filter(e -> e.getState() == 20 || e.getState() == 30)
                    .map(e -> {
                        SpecSortDto specSortDto = new SpecSortDto();
                        specSortDto.setSpecId(e.getId());
                        if (Objects.nonNull(specAttMap.get(e.getId()))) {
                            specSortDto.setAtt(specAttMap.get(e.getId()));
                        } else {
                            specSortDto.setAtt(0);
                        }
                        return specSortDto;
                    })
                    .sorted((o1, o2) -> o2.getAtt() - o1.getAtt())
                    .map(SpecSortDto::getSpecId)
                    .toList();
        }
    }

    public static String getPriceInfo(int price) {
        String priceInfo = "";
        try {
            if (price != 0) {
                priceInfo = String.format("%.2f", price / 10000.0) + "万";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceInfo;
    }

    /**
     * price单位转换，只保留一位小数，当小数为0时去除小数
     *
     * @param price
     * @return
     */
    public static String getPriceWithOneDecimal(int price) {
        String priceInfo = "";
        //产品新增逻辑：降价金额在1000元以下时，直接展示xx元
        if (price > 0 && price < 1000)
            return price + "元";
        try {
            if (price != 0) {
                priceInfo = String.format("%.1f", price / 10000.0);
                if (priceInfo.endsWith("0"))
                    priceInfo = priceInfo.substring(0, priceInfo.length() - 2) + "万";
                else
                    priceInfo = priceInfo + "万";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceInfo;
    }

    /**
     * 字符串日期转换："2024-07-22" -> "07/22"
     *
     * @param dateStr
     * @return
     */
    public static String formatDate(String dateStr) {
        String formattedDate = "";
        try {
            // 定义输入日期的格式
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 解析输入字符串为LocalDate对象
            LocalDate date = LocalDate.parse(dateStr, inputFormatter);

            // 定义输出日期的格式
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd");
            // 格式化LocalDate对象为目标字符串格式
            formattedDate = date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }


    /**
     * 获取筛选项
     *
     * @param request
     * @return
     */
    public CarsHangqingSearchOptionsResponse.Result.Builder getSearchOptions(CarsHangqingSearchOptionsRequest request) {
        CarsHangqingSearchOptionsResponse.Result.Builder resultBuilder = CarsHangqingSearchOptionsResponse.Result.newBuilder();
        CarsHangqingSearchOptionsResponse.Result.Baseinfo.Builder basInfo = CarsHangqingSearchOptionsResponse.Result.Baseinfo.newBuilder();
        CitySortHangqingDto important_down_list = null;//重要降价车系
        CitySortHangqingDto non_important_down_list = null;//非重要降价车系
        List<HangqingSearchOptionsGroupDto> groupDtos = Collections.emptyList();
        try {
            HangqingHeadConfig headConfig = JsonUtil.toObject(hangqingheadconfig, new TypeReference<HangqingHeadConfig>() {
            });
            if (headConfig != null) {
                basInfo.setBgpic(headConfig.getBgpic());
                basInfo.setBgcolor(headConfig.getBgcolor());
                basInfo.setTitlepic(headConfig.getTitlepic());
                basInfo.setSubtitle(headConfig.getSubtitle());
            }


            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.0")) {
                important_down_list = citySortHangqingComponent.get(request.getCityid(), 51);//组件获取重要降价车系
                non_important_down_list = citySortHangqingComponent.get(request.getCityid(), 5);//组件获取近期降价模块数据(非重要降价车系)
                groupDtos = JsonUtil.toObject(hangqingsearch_option_11650, new TypeReference<List<HangqingSearchOptionsGroupDto>>() {
                });
                if (non_important_down_list == null || non_important_down_list.getDtoList() == null || non_important_down_list.getDtoList().isEmpty()) {
                    groupDtos.forEach(groupDto -> {
                        //当前城市无近期降价的车系时，去除“近期降价”的排序
                        if ("orderid".equals(groupDto.getKey()))
                            groupDto.getList().removeIf(listDTO -> "近期降价".equals(listDTO.getName()));
                    });
                }
            } else {
                groupDtos = JsonUtil.toObject(hangqingsearch_option, new TypeReference<List<HangqingSearchOptionsGroupDto>>() {
                });
            }

            if (groupDtos != null && groupDtos.size() > 0) {
                CarsHangqingSearchOptionsResponse.Result.ResultList.Builder allListBuilder = CarsHangqingSearchOptionsResponse.Result.ResultList.newBuilder();
                List<CarsHangqingSearchOptionsResponse.Result.ResultList.Grouplist> groupList = new ArrayList<>();
                for (HangqingSearchOptionsGroupDto item : groupDtos) {
                    CarsHangqingSearchOptionsResponse.Result.ResultList.Grouplist.Builder groupBuilder = CarsHangqingSearchOptionsResponse.Result.ResultList.Grouplist.newBuilder();
                    List<CarsHangqingSearchOptionsResponse.Result.ResultList.Grouplist.List> listDto = new ArrayList<>();
                    groupBuilder.setKey(item.getKey());
                    groupBuilder.setShowname(item.getShowname());
                    groupBuilder.setIsselectmore(item.getIsselectmore());
                    if (item.getList() != null && item.getList().size() > 0) {
                        item.getList().forEach(dto -> {
                            CarsHangqingSearchOptionsResponse.Result.ResultList.Grouplist.List.Builder listBuilder = CarsHangqingSearchOptionsResponse.Result.ResultList.Grouplist.List.newBuilder();
                            listBuilder.setName(dto.getName());
                            listBuilder.setValue(dto.getValue());
                            listBuilder.setKey(dto.getKey());
                            listBuilder.setParametername(dto.getParametername());
                            List<CarsHangqingSearchOptionsResponse.Result.ResultList.Grouplist.List.Childrenlist> childrenList = new ArrayList<>();
                            if (dto.getChildrenlist() != null && dto.getChildrenlist().size() > 0) {
                                dto.getChildrenlist().forEach(p -> {
                                    CarsHangqingSearchOptionsResponse.Result.ResultList.Grouplist.List.Childrenlist.Builder childrenlistBuilder = CarsHangqingSearchOptionsResponse.Result.ResultList.Grouplist.List.Childrenlist.newBuilder();
                                    childrenlistBuilder.setKey(p.getKey());
                                    childrenlistBuilder.setName(p.getName());
                                    childrenlistBuilder.setValue(p.getValue());
                                    childrenlistBuilder.setParametername(p.getParametername());
                                    childrenList.add(childrenlistBuilder.build());
                                });
                                listBuilder.addAllChildrenlist(childrenList);
                            }
                            listDto.add(listBuilder.build());
                        });
                        groupBuilder.addAllList(listDto);
                    }
                    groupList.add(groupBuilder.build());
                }
                allListBuilder.addAllGrouplist(groupList);
                resultBuilder.addResultlist(allListBuilder);
            }
        } catch (Exception ex) {
            log.error("getSearchOptions异常-ex:{}", ex);
        }
        try {
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.0")) {//11.65.0版本增加"新降"模块
                int cases;
                if (null == important_down_list && null == non_important_down_list) {
                    cases = 1;//重要车系和非重要车系都无数据
                } else if (null == important_down_list && null != non_important_down_list) {
                    cases = 2;//只有非重要车系有数据
                } else if (null != important_down_list && null == non_important_down_list) {
                    cases = 3;//只有重要车系有数据
                } else {
                    cases = 4;//两种类型的车系都有数据
                }
                switch (cases) {
                    case 1:
                        basInfo.addAllList(Collections.emptyList());
                        break;
                    case 2:
                        nonImportHavaDate(request, non_important_down_list, basInfo);
                        break;
                    case 3:
                        importHavaDate(request, important_down_list, basInfo);
                        break;
                    case 4:
                        bothHavaDate(request, important_down_list, non_important_down_list, basInfo);
                        break;
                    default:
                }
            }
            if (basInfo.getListList() != null && !basInfo.getListList().isEmpty())
                basInfo.setBgpic("http://nfiles3.autohome.com.cn/zrjcpk10/newhangqing11650.png");//新降模块有数据，则使用新头图
        } catch (Exception e) {
            log.error("getSearchOptions-新降模块异常-ex:{}", e);
        }
        resultBuilder.setBaseinfo(basInfo);
        return resultBuilder;
    }

    private void importHavaDate(CarsHangqingSearchOptionsRequest request, CitySortHangqingDto important_down_list, CarsHangqingSearchOptionsResponse.Result.Baseinfo.Builder basInfo) {
        if (null == important_down_list.getDtoList() || important_down_list.getDtoList().isEmpty())
            return;
        AtomicInteger relay_index = new AtomicInteger(1);
        List<Integer> seriesIdList = important_down_list.getDtoList().stream().map(CitySortHangqingDto.HangqingDto::getSeriesId).toList();
        List<CompletableFuture> prepareTasks = new ArrayList<>();
        AtomicReference<List<SeriesDetailDto>> seriesDetailListRef = new AtomicReference<>();
        AtomicReference<List<SeriesSpecDto>> SeriesSpecListRef = new AtomicReference<>();
        AtomicReference<List<SeriesAttentionDto>> seriesAttentionListRef = new AtomicReference<>();
        //通过组件获取获取"重要降价车系"的相关数据
        prepareTasks.add(seriesDetailComponent.getList(seriesIdList)
                .thenAccept(seriesDetailListRef::set).exceptionally(e -> null));
        prepareTasks.add(seriesSpecComponent.get(seriesIdList)
                .thenAccept(SeriesSpecListRef::set).exceptionally(e -> null));
        prepareTasks.add(seriesAttentionComponent.getList(seriesIdList)
                .thenAccept(seriesAttentionListRef::set).exceptionally(e -> null));
        //重要降价车系-车系详情、车系下的所有车型、车系关注度
        Map<Integer, SeriesDetailDto> seriesDetailDtoMap = seriesDetailListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity()));
        Map<Integer, SeriesSpecDto> seriesSpecDtoMap = SeriesSpecListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesSpecDto::getSeriesId, Function.identity()));
        Map<Integer, SeriesAttentionDto> seriesAttentionDtoMap = seriesAttentionListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesAttentionDto::getSeriesId, Function.identity()));
        //获取重要降价车系下的 车型详情、车型在对应城市的询价
        List<CompletableFuture> seriesSpecTasks = new ArrayList<>();
        ConcurrentHashMap<Integer, SpecDetailDto> specDetailMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecCityAskPriceDto> specCityAskPriceMap = new ConcurrentHashMap<>();
        seriesSpecDtoMap.values().forEach(seriesSpecDto -> {
            List<Integer> specIdList = seriesSpecDto.getItems().stream()
                    .filter(e -> e.getState() == 20 || e.getState() == 30)
                    .map(SeriesSpecDto.Item::getId)
                    .toList();
            seriesSpecTasks.add(specDetailComponent.getList(specIdList)
                    .thenAccept(r -> r.forEach(spec -> specDetailMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
            seriesSpecTasks.add(specCityAskPriceComponent.getListAsync(specIdList, request.getCityid())
                    .thenAccept(r -> r.forEach(spec -> specCityAskPriceMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
        });
        //先组装重要降价车系
        for (int i = 0; i < Math.min(10, important_down_list.getDtoList().size()); i++) {//最多组装10条数据(重要车系可能少于10个)
            CitySortHangqingDto.HangqingDto dto = important_down_list.getDtoList().get(i);
            CarsHangqingSearchOptionsResponse.Result.Baseinfo.LatestDownList.Builder downListDto = CarsHangqingSearchOptionsResponse.Result.Baseinfo.LatestDownList.newBuilder();
            SeriesAttentionDto seriesAttentionDto = seriesAttentionDtoMap.get(dto.getSeriesId());
            Map<Integer, CitySortHangqingDto.SpecPriceOffInfo> specPriceOffInfoMap = dto.getPriceOffInfo().getSpecPriceOffInfoList()
                    .stream()
                    .collect(Collectors.toMap(CitySortHangqingDto.SpecPriceOffInfo::getSpecId, Function.identity()));
            //按照车系下车型关注度获取车型列表
            Map<Integer, Integer> specAttMap;
            if (Objects.nonNull(seriesAttentionDto)) {
                specAttMap = seriesAttentionDto.getSpecAttentions().stream()
                        .collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                                SeriesAttentionDto.SpecAttention::getAttention));
            } else {
                specAttMap = new HashMap<>();
            }
            Optional<CitySortHangqingDto.SpecPriceOffInfo> highestAttSpec = dto.getPriceOffInfo().getSpecPriceOffInfoList()
                    .stream()
                    .sorted((o1, o2) -> {
                        //先根据降价日期倒排
                        String o1_dt = specPriceOffInfoMap.get(o1.getSpecId()).getDt();
                        String o2_dt = specPriceOffInfoMap.get(o2.getSpecId()).getDt();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
                        Date o1_date = null;
                        Date o2_date = null;
                        try {
                            o1_date = simpleDateFormat.parse(o1_dt);
                            o2_date = simpleDateFormat.parse(o2_dt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        int compare_result = o2_date.compareTo(o1_date);
                        if (compare_result != 0) {
                            return compare_result;//两个车型的降价日期不相同，直接返回
                        }
                        //两个车型的降价日期相同，再根据关注度倒排
                        if (Objects.nonNull(specAttMap.get(o1.getSpecId()))
                                && Objects.nonNull(specAttMap.get(o2.getSpecId()))) {
                            return specAttMap.get(o2.getSpecId()) - specAttMap.get(o1.getSpecId());
                        } else if (Objects.nonNull(specAttMap.get(o1.getSpecId()))) {
                            return -1;
                        } else {
                            return 1;
                        }
                    })
                    .findFirst();

            downListDto.setType(1);
            downListDto.setPicurl("https://nfiles3.autohome.com.cn/zrjcpk10/20240725xinjiang.png");
            downListDto.setSeriesname(seriesDetailDtoMap.get(dto.getSeriesId()).getName());
            if (highestAttSpec.isPresent()) {
                downListDto.setDate(formatDate(specPriceOffInfoMap.get(highestAttSpec.get().getSpecId()).getDt()));
                downListDto.setPricetxt(getPriceInfo(specCityAskPriceMap.get(highestAttSpec.get().getSpecId()).getMinPrice()) + "起");
                downListDto.setChangepricetxt(getPriceWithOneDecimal(specPriceOffInfoMap.get(highestAttSpec.get().getSpecId()).getPriceOff()));
                downListDto.setPricedecrease(true);
                downListDto.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                        seriesDetailDtoMap.get(dto.getSeriesId()).getBrandId(), dto.getSeriesId(), highestAttSpec.get().getSpecId(),
                        UrlUtil.encode(seriesDetailDtoMap.get(dto.getSeriesId()).getName()), 8, UrlUtil.encode("本地报价")));
            }

            CarsHangqingSearchOptionsResponse.Result.Baseinfo.RightInfo.Builder rightInfo = CarsHangqingSearchOptionsResponse.Result.Baseinfo.RightInfo.newBuilder();
            rightInfo.setText("更多");
            rightInfo.setOrderid("5");
            CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Builder rightInfo_pvItem = CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.newBuilder()
                    .putArgvs("seriesid", String.valueOf(dto.getSeriesId()))
                    .putArgvs("cityid", String.valueOf(request.getCityid()))
                    .putArgvs("type", "2")
                    .putArgvs("index", String.valueOf(relay_index.get()))
                    .setClick(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Click.newBuilder().setEventid("carmarketchannel_xinjiang_card_click"));
            rightInfo.setPvitem(rightInfo_pvItem);

            CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Builder pvItem = CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.newBuilder()
                    .putArgvs("seriesid", String.valueOf(dto.getSeriesId()))
                    .putArgvs("cityid", String.valueOf(request.getCityid()))
                    .putArgvs("type", "1")
                    .putArgvs("index", String.valueOf(relay_index.get()))
                    .setClick(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Click.newBuilder().setEventid("carmarketchannel_xinjiang_card_click").putArgvs("importcar","1"))
                    .setShow(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Show.newBuilder().setEventid("carmarketchannel_xinjiang_card_show").putArgvs("importcar", "1"));
            downListDto.setRightinfo(rightInfo);
            downListDto.setPvitem(pvItem);
            relay_index.getAndIncrement();
            basInfo.addList(downListDto);
        }
    }

    private void nonImportHavaDate(CarsHangqingSearchOptionsRequest request, CitySortHangqingDto non_important_down_list, CarsHangqingSearchOptionsResponse.Result.Baseinfo.Builder basInfo) {
        if (null == non_important_down_list.getDtoList() || non_important_down_list.getDtoList().isEmpty())
            return;
        AtomicInteger relay_index = new AtomicInteger(1);
        List<Integer> non_important_seriesIdList = non_important_down_list.getDtoList().stream().map(CitySortHangqingDto.HangqingDto::getSeriesId).toList();
        List<CompletableFuture> prepareTasks = new ArrayList<>();
        AtomicReference<List<SeriesDetailDto>> non_important_seriesDetailListRef = new AtomicReference<>();
        AtomicReference<List<SeriesSpecDto>> non_important_SeriesSpecListRef = new AtomicReference<>();
        AtomicReference<List<SeriesAttentionDto>> non_important_seriesAttentionListRef = new AtomicReference<>();
        //通过组件获取获取"非重要降价车系"的相关数据
        prepareTasks.add(seriesDetailComponent.getList(non_important_seriesIdList)
                .thenAccept(non_important_seriesDetailListRef::set).exceptionally(e -> null));
        prepareTasks.add(seriesSpecComponent.get(non_important_seriesIdList)
                .thenAccept(non_important_SeriesSpecListRef::set).exceptionally(e -> null));
        prepareTasks.add(seriesAttentionComponent.getList(non_important_seriesIdList)
                .thenAccept(non_important_seriesAttentionListRef::set).exceptionally(e -> null));
        CompletableFuture.allOf(prepareTasks.toArray(new CompletableFuture[0])).join();
        //非重要降价车系-车系详情、车系下的所有车型、车系关注度
        Map<Integer, SeriesDetailDto> non_important_seriesDetailDtoMap = non_important_seriesDetailListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity()));
        Map<Integer, SeriesSpecDto> non_important_seriesSpecDtoMap = non_important_SeriesSpecListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesSpecDto::getSeriesId, Function.identity()));
        Map<Integer, SeriesAttentionDto> non_important_seriesAttentionDtoMap = non_important_seriesAttentionListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesAttentionDto::getSeriesId, Function.identity()));
        //非重要降价车系通过组件获取车型详情信息、车型在对应城市的询价
        List<CompletableFuture> seriesSpecTasks = new ArrayList<>();
        ConcurrentHashMap<Integer, SpecDetailDto> non_important_specDetailMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecCityAskPriceDto> non_important_specCityAskPriceMap = new ConcurrentHashMap<>();
        non_important_seriesSpecDtoMap.values().forEach(seriesSpecDto -> {
            List<Integer> specIdList = seriesSpecDto.getItems().stream()
                    .filter(e -> e.getState() == 20 || e.getState() == 30)
                    .map(SeriesSpecDto.Item::getId)
                    .toList();
            seriesSpecTasks.add(specDetailComponent.getList(specIdList)
                    .thenAccept(r -> r.forEach(spec -> non_important_specDetailMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
            seriesSpecTasks.add(specCityAskPriceComponent.getListAsync(specIdList, request.getCityid())
                    .thenAccept(r -> r.forEach(spec -> non_important_specCityAskPriceMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
        });
        CompletableFuture.allOf(seriesSpecTasks.toArray(new CompletableFuture[0])).join();
        for (int i = 0; i < Math.min(10, non_important_down_list.getDtoList().size()); i++) {//只需要组装10条
            CitySortHangqingDto.HangqingDto dto = non_important_down_list.getDtoList().get(i);
            CarsHangqingSearchOptionsResponse.Result.Baseinfo.LatestDownList.Builder downListDto = CarsHangqingSearchOptionsResponse.Result.Baseinfo.LatestDownList.newBuilder();
            SeriesAttentionDto seriesAttentionDto = non_important_seriesAttentionDtoMap.get(dto.getSeriesId());
            Map<Integer, CitySortHangqingDto.SpecPriceOffInfo> specPriceOffInfoMap = dto.getPriceOffInfo().getSpecPriceOffInfoList()
                    .stream()
                    .collect(Collectors.toMap(CitySortHangqingDto.SpecPriceOffInfo::getSpecId, Function.identity()));
            //按照车系下车型关注度获取车型列表
            Map<Integer, Integer> specAttMap;
            if (Objects.nonNull(seriesAttentionDto)) {
                specAttMap = seriesAttentionDto.getSpecAttentions().stream()
                        .collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                                SeriesAttentionDto.SpecAttention::getAttention));
            } else {
                specAttMap = new HashMap<>();
            }

            Optional<CitySortHangqingDto.SpecPriceOffInfo> highestAttSpec = dto.getPriceOffInfo().getSpecPriceOffInfoList()
                    .stream()
                    .sorted((o1, o2) -> {
                        //先根据降价日期倒排
                        String o1_dt = specPriceOffInfoMap.get(o1.getSpecId()).getDt();
                        String o2_dt = specPriceOffInfoMap.get(o2.getSpecId()).getDt();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
                        Date o1_date = null;
                        Date o2_date = null;
                        try {
                            o1_date = simpleDateFormat.parse(o1_dt);
                            o2_date = simpleDateFormat.parse(o2_dt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        int compare_result = o2_date.compareTo(o1_date);
                        if (compare_result != 0) {
                            return compare_result;//两个车型的降价日期不相同，直接返回
                        }
                        //两个车型的降价日期相同，再根据关注度倒排
                        if (Objects.nonNull(specAttMap.get(o1.getSpecId()))
                                && Objects.nonNull(specAttMap.get(o2.getSpecId()))) {
                            return specAttMap.get(o2.getSpecId()) - specAttMap.get(o1.getSpecId());
                        } else if (Objects.nonNull(specAttMap.get(o1.getSpecId()))) {
                            return -1;
                        } else {
                            return 1;
                        }
                    })
                    .findFirst();

            downListDto.setType(1);
            downListDto.setPicurl("https://nfiles3.autohome.com.cn/zrjcpk10/20240725xinjiang.png");
            downListDto.setSeriesname(non_important_seriesDetailDtoMap.get(dto.getSeriesId()).getName());
            if (highestAttSpec.isPresent()) {
                downListDto.setDate(formatDate(specPriceOffInfoMap.get(highestAttSpec.get().getSpecId()).getDt()));
                downListDto.setPricetxt(getPriceInfo(non_important_specCityAskPriceMap.get(highestAttSpec.get().getSpecId()).getMinPrice()) + "起");
                downListDto.setChangepricetxt(getPriceWithOneDecimal(specPriceOffInfoMap.get(highestAttSpec.get().getSpecId()).getPriceOff()));
                downListDto.setPricedecrease(true);
                downListDto.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                        non_important_seriesDetailDtoMap.get(dto.getSeriesId()).getBrandId(), dto.getSeriesId(), highestAttSpec.get().getSpecId(),
                        UrlUtil.encode(non_important_seriesDetailDtoMap.get(dto.getSeriesId()).getName()), 8, UrlUtil.encode("本地报价")));
            }

            CarsHangqingSearchOptionsResponse.Result.Baseinfo.RightInfo.Builder rightInfo = CarsHangqingSearchOptionsResponse.Result.Baseinfo.RightInfo.newBuilder();
            rightInfo.setText("更多");
            rightInfo.setOrderid("5");
            CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Builder rightInfo_pvItem = CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.newBuilder()
                    .putArgvs("seriesid", String.valueOf(dto.getSeriesId()))
                    .putArgvs("cityid", String.valueOf(request.getCityid()))
                    .putArgvs("type", "2")
                    .putArgvs("index", String.valueOf(relay_index.get()))
                    .setClick(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Click.newBuilder().setEventid("carmarketchannel_xinjiang_card_click"));
            rightInfo.setPvitem(rightInfo_pvItem);

            CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Builder pvItem = CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.newBuilder()
                    .putArgvs("seriesid", String.valueOf(dto.getSeriesId()))
                    .putArgvs("cityid", String.valueOf(request.getCityid()))
                    .putArgvs("type", "1")
                    .putArgvs("index", String.valueOf(relay_index.get()))
                    .setClick(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Click.newBuilder().setEventid("carmarketchannel_xinjiang_card_click")).putArgvs("importcar","0")
                    .setShow(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Show.newBuilder().setEventid("carmarketchannel_xinjiang_card_show").putArgvs("importcar", "0"));
            downListDto.setRightinfo(rightInfo);
            downListDto.setPvitem(pvItem);
            relay_index.getAndIncrement();
            basInfo.addList(downListDto);
        }
    }

    private void bothHavaDate(CarsHangqingSearchOptionsRequest request, CitySortHangqingDto important_down_list, CitySortHangqingDto non_important_down_list, CarsHangqingSearchOptionsResponse.Result.Baseinfo.Builder basInfo) {
        AtomicInteger relay_index = new AtomicInteger(1);
        List<Integer> seriesIdList = important_down_list.getDtoList().stream().map(CitySortHangqingDto.HangqingDto::getSeriesId).toList();
        List<Integer> non_important_seriesIdList = non_important_down_list.getDtoList().stream().map(CitySortHangqingDto.HangqingDto::getSeriesId).toList();
        List<CompletableFuture> prepareTasks = new ArrayList<>();
        AtomicReference<List<SeriesDetailDto>> seriesDetailListRef = new AtomicReference<>();
        AtomicReference<List<SeriesSpecDto>> SeriesSpecListRef = new AtomicReference<>();
        AtomicReference<List<SeriesAttentionDto>> seriesAttentionListRef = new AtomicReference<>();

        AtomicReference<List<SeriesDetailDto>> non_important_seriesDetailListRef = new AtomicReference<>();
        AtomicReference<List<SeriesSpecDto>> non_important_SeriesSpecListRef = new AtomicReference<>();
        AtomicReference<List<SeriesAttentionDto>> non_important_seriesAttentionListRef = new AtomicReference<>();
        //通过组件获取获取"重要降价车系"的相关数据
        prepareTasks.add(seriesDetailComponent.getList(seriesIdList)
                .thenAccept(seriesDetailListRef::set).exceptionally(e -> null));
        prepareTasks.add(seriesSpecComponent.get(seriesIdList)
                .thenAccept(SeriesSpecListRef::set).exceptionally(e -> null));
        prepareTasks.add(seriesAttentionComponent.getList(seriesIdList)
                .thenAccept(seriesAttentionListRef::set).exceptionally(e -> null));
        //通过组件获取获取"非重要降价车系"的相关数据
        prepareTasks.add(seriesDetailComponent.getList(non_important_seriesIdList)
                .thenAccept(non_important_seriesDetailListRef::set).exceptionally(e -> null));
        prepareTasks.add(seriesSpecComponent.get(non_important_seriesIdList)
                .thenAccept(non_important_SeriesSpecListRef::set).exceptionally(e -> null));
        prepareTasks.add(seriesAttentionComponent.getList(non_important_seriesIdList)
                .thenAccept(non_important_seriesAttentionListRef::set).exceptionally(e -> null));
        CompletableFuture.allOf(prepareTasks.toArray(new CompletableFuture[0])).join();

        //重要降价车系-车系详情、车系下的所有车型、车系关注度
        Map<Integer, SeriesDetailDto> seriesDetailDtoMap = seriesDetailListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity()));
        Map<Integer, SeriesSpecDto> seriesSpecDtoMap = SeriesSpecListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesSpecDto::getSeriesId, Function.identity()));
        Map<Integer, SeriesAttentionDto> seriesAttentionDtoMap = seriesAttentionListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesAttentionDto::getSeriesId, Function.identity()));
        //非重要降价车系-车系详情、车系下的所有车型、车系关注度
        Map<Integer, SeriesDetailDto> non_important_seriesDetailDtoMap = non_important_seriesDetailListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity()));
        Map<Integer, SeriesSpecDto> non_important_seriesSpecDtoMap = non_important_SeriesSpecListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesSpecDto::getSeriesId, Function.identity()));
        Map<Integer, SeriesAttentionDto> non_important_seriesAttentionDtoMap = non_important_seriesAttentionListRef.get().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SeriesAttentionDto::getSeriesId, Function.identity()));

        //获取重要降价车系下的 车型详情、车型在对应城市的询价
        List<CompletableFuture> seriesSpecTasks = new ArrayList<>();
        ConcurrentHashMap<Integer, SpecDetailDto> specDetailMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecCityAskPriceDto> specCityAskPriceMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecDetailDto> non_important_specDetailMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecCityAskPriceDto> non_important_specCityAskPriceMap = new ConcurrentHashMap<>();
        seriesSpecDtoMap.values().forEach(seriesSpecDto -> {
            List<Integer> specIdList = seriesSpecDto.getItems().stream()
                    .filter(e -> e.getState() == 20 || e.getState() == 30)
                    .map(SeriesSpecDto.Item::getId)
                    .toList();
            seriesSpecTasks.add(specDetailComponent.getList(specIdList)
                    .thenAccept(r -> r.forEach(spec -> specDetailMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
            seriesSpecTasks.add(specCityAskPriceComponent.getListAsync(specIdList, request.getCityid())
                    .thenAccept(r -> r.forEach(spec -> specCityAskPriceMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
        });
        //非重要降价车系通过组件获取车型详情信息、车型在对应城市的询价
        non_important_seriesSpecDtoMap.values().forEach(seriesSpecDto -> {
            List<Integer> specIdList = seriesSpecDto.getItems().stream()
                    .filter(e -> e.getState() == 20 || e.getState() == 30)
                    .map(SeriesSpecDto.Item::getId)
                    .toList();
            seriesSpecTasks.add(specDetailComponent.getList(specIdList)
                    .thenAccept(r -> r.forEach(spec -> non_important_specDetailMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
            seriesSpecTasks.add(specCityAskPriceComponent.getListAsync(specIdList, request.getCityid())
                    .thenAccept(r -> r.forEach(spec -> non_important_specCityAskPriceMap.put(spec.getSpecId(), spec)))
                    .exceptionally(e -> null));
        });
        CompletableFuture.allOf(seriesSpecTasks.toArray(new CompletableFuture[0])).join();

        //先组装重要降价车系
        if (important_down_list.getDtoList() != null || !important_down_list.getDtoList().isEmpty()) {
            important_down_list.getDtoList()
                    .forEach(dto -> {
                        CarsHangqingSearchOptionsResponse.Result.Baseinfo.LatestDownList.Builder downListDto = CarsHangqingSearchOptionsResponse.Result.Baseinfo.LatestDownList.newBuilder();
                        SeriesAttentionDto seriesAttentionDto = seriesAttentionDtoMap.get(dto.getSeriesId());
                        Map<Integer, CitySortHangqingDto.SpecPriceOffInfo> specPriceOffInfoMap = dto.getPriceOffInfo().getSpecPriceOffInfoList()
                                .stream()
                                .collect(Collectors.toMap(CitySortHangqingDto.SpecPriceOffInfo::getSpecId, Function.identity()));
                        //按照车系下车型关注度获取车型列表
                        Map<Integer, Integer> specAttMap;
                        if (Objects.nonNull(seriesAttentionDto)) {
                            specAttMap = seriesAttentionDto.getSpecAttentions().stream()
                                    .collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                                            SeriesAttentionDto.SpecAttention::getAttention));
                        } else {
                            specAttMap = new HashMap<>();
                        }

                        Optional<CitySortHangqingDto.SpecPriceOffInfo> highestAttSpec = dto.getPriceOffInfo().getSpecPriceOffInfoList()
                                .stream()
                                .sorted((o1, o2) -> {
                                    //先根据降价日期倒排
                                    String o1_dt = specPriceOffInfoMap.get(o1.getSpecId()).getDt();
                                    String o2_dt = specPriceOffInfoMap.get(o2.getSpecId()).getDt();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
                                    Date o1_date = null;
                                    Date o2_date = null;
                                    try {
                                        o1_date = simpleDateFormat.parse(o1_dt);
                                        o2_date = simpleDateFormat.parse(o2_dt);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    int compare_result = o2_date.compareTo(o1_date);
                                    if (compare_result != 0) {
                                        return compare_result;//两个车型的降价日期不相同，直接返回
                                    }
                                    //两个车型的降价日期相同，再根据关注度倒排
                                    if (Objects.nonNull(specAttMap.get(o1.getSpecId()))
                                            && Objects.nonNull(specAttMap.get(o2.getSpecId()))) {
                                        return specAttMap.get(o2.getSpecId()) - specAttMap.get(o1.getSpecId());
                                    } else if (Objects.nonNull(specAttMap.get(o1.getSpecId()))) {
                                        return -1;
                                    } else {
                                        return 1;
                                    }
                                })
                                .findFirst();

                        downListDto.setType(1);
                        downListDto.setPicurl("https://nfiles3.autohome.com.cn/zrjcpk10/20240725xinjiang.png");
                        downListDto.setSeriesname(seriesDetailDtoMap.get(dto.getSeriesId()).getName());
                        if (highestAttSpec.isPresent()) {
                            downListDto.setDate(formatDate(specPriceOffInfoMap.get(highestAttSpec.get().getSpecId()).getDt()));
                            downListDto.setPricetxt(specCityAskPriceMap.get(highestAttSpec.get().getSpecId()) == null ? "" : getPriceInfo(specCityAskPriceMap.get(highestAttSpec.get().getSpecId()).getMinPrice()) + "起");
                            downListDto.setChangepricetxt(getPriceWithOneDecimal(specPriceOffInfoMap.get(highestAttSpec.get().getSpecId()).getPriceOff()));
                            downListDto.setPricedecrease(true);
                            downListDto.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                                    seriesDetailDtoMap.get(dto.getSeriesId()).getBrandId(), dto.getSeriesId(), highestAttSpec.get().getSpecId(),
                                    UrlUtil.encode(seriesDetailDtoMap.get(dto.getSeriesId()).getName()), 8, UrlUtil.encode("本地报价")));
                        }

                        CarsHangqingSearchOptionsResponse.Result.Baseinfo.RightInfo.Builder rightInfo = CarsHangqingSearchOptionsResponse.Result.Baseinfo.RightInfo.newBuilder();
                        rightInfo.setText("更多");
                        rightInfo.setOrderid("5");
                        CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Builder rightInfo_pvItem = CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.newBuilder()
                                .putArgvs("seriesid", String.valueOf(dto.getSeriesId()))
                                .putArgvs("cityid", String.valueOf(request.getCityid()))
                                .putArgvs("type", "2")
                                .putArgvs("index", String.valueOf(relay_index.get()))
                                .setClick(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Click.newBuilder().setEventid("carmarketchannel_xinjiang_card_click"));
                        rightInfo.setPvitem(rightInfo_pvItem);

                        CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Builder pvItem = CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.newBuilder()
                                .putArgvs("seriesid", String.valueOf(dto.getSeriesId()))
                                .putArgvs("cityid", String.valueOf(request.getCityid()))
                                .putArgvs("type", "1")
                                .putArgvs("index", String.valueOf(relay_index.get()))
                                .setClick(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Click.newBuilder().setEventid("carmarketchannel_xinjiang_card_click")).putArgvs("importcar","1")
                                .setShow(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Show.newBuilder().setEventid("carmarketchannel_xinjiang_card_show").putArgvs("importcar", "1"));
                        downListDto.setRightinfo(rightInfo);
                        downListDto.setPvitem(pvItem);
                        relay_index.getAndIncrement();
                        basInfo.addList(downListDto);
                    });
        }
        //重要降价车系的数量不足10条时，走下面的逻辑，组装非重要降价车系
        if (basInfo.getListList().size() < 10 && non_important_down_list.getDtoList() != null && !non_important_down_list.getDtoList().isEmpty()) {
            int leftSize = 10 - basInfo.getListList().size();//剩余需要填充的条数
            int nonImport_size = non_important_down_list.getDtoList().size();//非重要降价车系的数量
            int loop = Math.min(leftSize, nonImport_size);
            for (int i = 0; i < Math.min(loop,nonImport_size); i++) {
                CitySortHangqingDto.HangqingDto dto = non_important_down_list.getDtoList().get(i);
                if (important_down_list.getDtoList().stream().anyMatch(hangqingDto -> hangqingDto.getSeriesId() == dto.getSeriesId())) {
                    //可能存在重复的车系，这里需要剔除重复车系，同时循环次数还要+1
                    loop++;
                    continue;
                }
                CarsHangqingSearchOptionsResponse.Result.Baseinfo.LatestDownList.Builder downListDto = CarsHangqingSearchOptionsResponse.Result.Baseinfo.LatestDownList.newBuilder();
                SeriesAttentionDto seriesAttentionDto = non_important_seriesAttentionDtoMap.get(dto.getSeriesId());
                Map<Integer, CitySortHangqingDto.SpecPriceOffInfo> specPriceOffInfoMap = dto.getPriceOffInfo().getSpecPriceOffInfoList()
                        .stream()
                        .collect(Collectors.toMap(CitySortHangqingDto.SpecPriceOffInfo::getSpecId, Function.identity()));
//                             按照车系下车型关注度获取车型列表
                Map<Integer, Integer> specAttMap;
                if (Objects.nonNull(seriesAttentionDto)) {
                    specAttMap = seriesAttentionDto.getSpecAttentions().stream()
                            .collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                                    SeriesAttentionDto.SpecAttention::getAttention));
                } else {
                    specAttMap = new HashMap<>();
                }

                Optional<CitySortHangqingDto.SpecPriceOffInfo> highestAttSpec = dto.getPriceOffInfo().getSpecPriceOffInfoList()
                        .stream()
                        .sorted((o1, o2) -> {
                            String o1_dt = specPriceOffInfoMap.get(o1.getSpecId()).getDt();
                            String o2_dt = specPriceOffInfoMap.get(o2.getSpecId()).getDt();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
                            Date o1_date = null;
                            Date o2_date = null;
                            try {
                                o1_date = simpleDateFormat.parse(o1_dt);
                                o2_date = simpleDateFormat.parse(o2_dt);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            int compare_result = o2_date.compareTo(o1_date);
                            if (compare_result != 0) {
                                return compare_result;//两个车型的降价日期不相同，直接返回
                            }
                            //两个车型的降价日期相同，再根据关注度进行倒排
                            if (Objects.nonNull(specAttMap.get(o1.getSpecId()))
                                    && Objects.nonNull(specAttMap.get(o2.getSpecId()))) {
                                return specAttMap.get(o2.getSpecId()) - specAttMap.get(o1.getSpecId());
                            } else if (Objects.nonNull(specAttMap.get(o1.getSpecId()))) {
                                return -1;
                            } else {
                                return 1;
                            }
                        })
                        .findFirst();

                downListDto.setType(1);
                downListDto.setPicurl("https://nfiles3.autohome.com.cn/zrjcpk10/20240725xinjiang.png");
                downListDto.setSeriesname(non_important_seriesDetailDtoMap.get(dto.getSeriesId()).getName());
                if (highestAttSpec.isPresent()) {
                    downListDto.setDate(formatDate(specPriceOffInfoMap.get(highestAttSpec.get().getSpecId()).getDt()));
                    downListDto.setPricetxt(getPriceInfo(non_important_specCityAskPriceMap.get(highestAttSpec.get().getSpecId()).getMinPrice()) + "起");
                    downListDto.setChangepricetxt(getPriceWithOneDecimal(specPriceOffInfoMap.get(highestAttSpec.get().getSpecId()).getPriceOff()));
                    downListDto.setPricedecrease(true);
                    downListDto.setJumpurl(String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&sourceid=%s&tabindex=1&fromtype=1&tabtype=1&tabpricename=%s",
                            non_important_seriesDetailDtoMap.get(dto.getSeriesId()).getBrandId(), dto.getSeriesId(), highestAttSpec.get().getSpecId(),
                            UrlUtil.encode(non_important_seriesDetailDtoMap.get(dto.getSeriesId()).getName()), 8, UrlUtil.encode("本地报价")));
                }

                CarsHangqingSearchOptionsResponse.Result.Baseinfo.RightInfo.Builder rightInfo = CarsHangqingSearchOptionsResponse.Result.Baseinfo.RightInfo.newBuilder();
                rightInfo.setText("更多");
                rightInfo.setOrderid("5");
                CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Builder rightInfo_pvItem = CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(dto.getSeriesId()))
                        .putArgvs("cityid", String.valueOf(request.getCityid()))
                        .putArgvs("type", "2")
                        .putArgvs("index", String.valueOf(relay_index.get()))
                        .setClick(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Click.newBuilder().setEventid("carmarketchannel_xinjiang_card_more_click"));
                rightInfo.setPvitem(rightInfo_pvItem);

                CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Builder pvItem = CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(dto.getSeriesId()))
                        .putArgvs("cityid", String.valueOf(request.getCityid()))
                        .putArgvs("type", "1")
                        .putArgvs("index", String.valueOf(relay_index.get()))
                        .setClick(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Click.newBuilder().setEventid("carmarketchannel_xinjiang_card_click")).putArgvs("importcar","0")
                        .setShow(CarsHangqingSearchOptionsResponse.Result.Baseinfo.PvItem.Show.newBuilder().setEventid("carmarketchannel_xinjiang_card_show").putArgvs("importcar", "0"));
                downListDto.setRightinfo(rightInfo);
                downListDto.setPvitem(pvItem);
                relay_index.getAndIncrement();
                basInfo.addList(downListDto);
            }
        }
    }
}
