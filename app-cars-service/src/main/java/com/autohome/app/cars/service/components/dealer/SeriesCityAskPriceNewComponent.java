package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerSpecPriceListResult;
import com.autohome.app.cars.apiclient.dealer.dtos.ListCshDealerByCityResult;
import com.autohome.app.cars.apiclient.dealer.dtos.SpecPriceItem;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.mapper.appcars.SeriesCityAskPriceNewMapper;
import com.autohome.app.cars.mapper.appcars.SpecDealerMinPriceMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecBaseInfoEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.SeriesSpecComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesSpecDto;
import com.autohome.app.cars.service.components.dealer.dtos.DealerAndPrice;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@DBConfig(tableName = "series_city_askprice_new")
@RedisConfig(keyVersion = "v6")
public class SeriesCityAskPriceNewComponent extends BaseComponent<SeriesCityAskPriceDto> {

    @Autowired
    DealerApiClient dealerApiClient;

    @Autowired
    SpecMapper specMapper;

    @Autowired
    SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    DealerSpecPriceComponent dealerSpecPriceComponent;

    @Autowired
    SeriesSpecComponent seriesSpecComponent;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";
    final static int countryId = 0;

    @Autowired
    SpecDealerMinPriceMapper specDealerMinPriceMapper;

    @Autowired
    private SeriesCityAskPriceNewMapper seriesCityAskPriceNewMapper;

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<List<SeriesCityAskPriceDto>> get(List<Integer> seriesIds, Integer cityId) {
        List<CompletableFuture> tasks = new ArrayList<>();
        List<SeriesCityAskPriceDto> dtoList = Collections.synchronizedList(new ArrayList<>());
        seriesIds.forEach(specId -> tasks.add(get(specId, cityId).thenApply(dtoList::add)));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        return CompletableFuture.completedFuture(dtoList);
    }

    public CompletableFuture<SeriesCityAskPriceDto> get(int seriesId, int cityId) {
        if (CityUtil.isGangAoTaiCity(cityId)) {
            return CompletableFuture.completedFuture(null);
        }
        return baseGetAsync(makeParam(seriesId, cityId)).thenCompose(info -> {
            if (info != null) {
                return CompletableFuture.completedFuture(info);
            }

            int provinceId = CityUtil.getProvinceId(cityId);
            return baseGetAsync(makeParam(seriesId, provinceId)).thenCompose(provinceInfo -> {
                if (provinceInfo != null) {
                    return CompletableFuture.completedFuture(provinceInfo);
                }
                return baseGetAsync(makeParam(seriesId, countryId)).thenCombine(baseGetAsync(makeParam(seriesId, provinceId * 100)), (country, province) -> {
                    if (country == null && province == null) {
                        return null;
                    } else if (country == null) {
                        return province;
                    } else if (province == null) {
                        return country;
                    } else {
                        SeriesCityAskPriceDto newDto = new SeriesCityAskPriceDto();
                        newDto.setMinPrice(province.getMinPrice() < country.getMinPrice() ? province.getMinPrice() : country.getMinPrice());
                        newDto.setMaxPrice(province.getMaxPrice() < country.getMaxPrice() ? province.getMaxPrice() : country.getMaxPrice());
                        return newDto;
                    }
                });
            });
        });
    }

    synchronized void add(ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> citySeiesSpecPrice, int city, int seriesId, int specId, DealerAndPrice dap) {
        ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>> seriesSpecPrice = citySeiesSpecPrice.computeIfAbsent(city, key -> new ConcurrentHashMap<>());
        //车系下所有车型
        ConcurrentMap<Integer, DealerAndPrice> specPrice = seriesSpecPrice.computeIfAbsent(seriesId, key -> new ConcurrentHashMap<>());
        //当旧值的补足类型小 || 补足类型一样价格小的时候用旧值，否则用新值

        specPrice.compute(specId, (ok, ov) ->
                ov != null && (ov.getBuzuType() < dap.getBuzuType() || (ov.getBuzuType() == dap.getBuzuType() && ov.getPrice() <= dap.getPrice()))
                        ? ov
                        : dap);
    }

    synchronized void addCountry(ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>> countrySeiesSpecPrice, int seriesId, int specId, DealerAndPrice dap) {
        //车系下所有车型
        ConcurrentMap<Integer, DealerAndPrice> specPrice = countrySeiesSpecPrice.computeIfAbsent(seriesId, key -> new ConcurrentHashMap<>());
        //当旧值的补足类型小 || 补足类型一样价格小的时候用旧值，否则用新值

        specPrice.compute(specId, (ok, ov) ->
                ov != null && (ov.getBuzuType() < dap.getBuzuType() || (ov.getBuzuType() == dap.getBuzuType() && ov.getPrice() <= dap.getPrice()))
                        ? ov
                        : dap);
    }

    synchronized void addPriceType(ConcurrentMap<Integer, ConcurrentMap<Integer, Integer>> citySeriesPriceType,
                                   int cityId,
                                   int seriesId,
                                   int priceType) {
        //城市下所有车系
        ConcurrentMap<Integer, Integer> seriesPriceType = citySeriesPriceType.computeIfAbsent(cityId, key -> new ConcurrentHashMap<>());
        //当旧的priceType不为空且更小时，优先使用旧值
        seriesPriceType.compute(seriesId, (ok, ov) ->
                ov != null && (ov < priceType)
                        ? ov
                        : priceType);
    }


    /**
     * 城市补足下放到城市，省级、全国补足，单独放
     *
     * @param xxlLog
     */
    public void refreshAll(Consumer<String> xxlLog) {
        List<SpecBaseInfoEntity> allSpecs = specMapper.getAllSpecBaseInfo();
        Map<Integer, SpecBaseInfoEntity> specs = allSpecs.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));

        //城市下每个车系下所有车型的最低价数据
        ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> citySeiesSpecPrice = new ConcurrentHashMap<>();
        //省下面各车系下各车型的最低价
        ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> provinceSeriesSpecPrice = new ConcurrentHashMap<>();
        //外省售本省下面各车系下各车型的最低价
        ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> otherProvinceSeriesSpecPrice = new ConcurrentHashMap<>();
        //全国各车系下车型的最低价
        ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>> countrySeriesSpecPrice = new ConcurrentHashMap<>();
        //城市下每个车系下经销商的priceType
        ConcurrentMap<Integer, ConcurrentMap<Integer, Integer>> citySeriesPriceType = new ConcurrentHashMap<>();


        //城市下每个车系下所有车型的最低价数据
        ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> citySeiesSpecPriceStopSale = new ConcurrentHashMap<>();
        //省下面各车系下各车型的最低价
        ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> provinceSeriesSpecPriceStopSale = new ConcurrentHashMap<>();
        //外省售本省下面各车系下各车型的最低价
        ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> otherProvinceSeriesSpecPriceStopSale = new ConcurrentHashMap<>();
        //全国各车系下车型的最低价
        ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>> countrySeriesSpecPriceStopSale = new ConcurrentHashMap<>();
        //城市下每个车系下经销商的priceType
        ConcurrentMap<Integer, ConcurrentMap<Integer, Integer>> citySeriesPriceTypeStopSale = new ConcurrentHashMap<>();
        //经销商车型报价列表数据
        List<DealerSpecPriceListResult> dealerSpecPriceList = new ArrayList<>();

        int cityCount = 0;
        List<Integer> cityIds = CityUtil.getAllCityIds();
        cityIds = cityIds.stream().sorted().collect(Collectors.toList());
        for (Integer cityId : cityIds) {
            dealerApiClient.getDealersByCityId(cityId).thenAccept(dealer -> {
                if (dealer == null || dealer.getResult() == null || dealer.getReturncode() != 0 || dealer.getResult().size() == 0) {
                    return;
                }

                List<CompletableFuture> tasks = new ArrayList<>();
                for (ListCshDealerByCityResult dealerInfo : dealer.getResult()) {
                    tasks.add(getSpecPriceListByDealerId(dealerInfo.getDealerId()).thenAccept(priceList -> {
                        if (priceList == null || priceList.getResult() == null || priceList.getReturncode() != 0 || priceList.getResult().size() == 0) {
                            return;
                        }
                        //经销商下车型报价列表
                        dealerSpecPriceList.add(getDealerSpecPriceDto(dealerInfo, priceList.getResult()));

                        for (SpecPriceItem specPriceItem : priceList.getResult()) {
                            int specId = specPriceItem.getSpecId();
                            try {
                                if (!specs.containsKey(specId)) continue;
                                SpecBaseInfoEntity spec = specs.get(specId);
                                int price = specPriceItem.getPrice();
                                int currentProvinceId = CityUtil.getProvinceId(cityId);
                                if (spec.getSpecState() == 40) {
                                    //肯定售本市
                                    add(citySeiesSpecPriceStopSale, cityId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 0));
                                    addPriceType(citySeriesPriceTypeStopSale, cityId, spec.getSeriesId(), 0);
                                    if (dealerInfo.getSaleScope() == 0) {  //售全国
                                        addCountry(countrySeriesSpecPriceStopSale, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 2));
                                        //售全国的，省内的补足优先级更高
                                        add(provinceSeriesSpecPriceStopSale, currentProvinceId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 1));
                                    } else if (dealerInfo.getSaleScope() == 1) { //售本省
                                        add(provinceSeriesSpecPriceStopSale, currentProvinceId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 1));
                                    } else if (dealerInfo.getSaleScope() == 2) { //售本市
                                    } else if (dealerInfo.getSaleScope() == 3) { //自定义
                                        if (dealerInfo.getSaleCity() != null && dealerInfo.getSaleCity().size() > 0) {
                                            for (Integer saleCity : dealerInfo.getRealSaleCitys()) {
                                                int buzuType = getBuzuType(saleCity, cityId);  //这里可能有跨省补足的数据
                                                add(citySeiesSpecPriceStopSale, saleCity, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), buzuType));
                                            }
                                        }
                                        if (dealerInfo.getSaleProvince() != null && dealerInfo.getSaleProvince().size() > 0) {
                                            for (Integer saleProvinceId : dealerInfo.getSaleProvince()) {
                                                if (saleProvinceId == currentProvinceId) {
                                                    add(provinceSeriesSpecPriceStopSale, saleProvinceId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 1));
                                                } else {
                                                    add(otherProvinceSeriesSpecPriceStopSale, saleProvinceId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 2));
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    //肯定售本市
                                    add(citySeiesSpecPrice, cityId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 0));
                                    addPriceType(citySeriesPriceType, cityId, spec.getSeriesId(), 0);
                                    if (dealerInfo.getSaleScope() == 0) {  //售全国
                                        addCountry(countrySeriesSpecPrice, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 2));
                                        //售全国的，省内的补足优先级更高
                                        add(provinceSeriesSpecPrice, currentProvinceId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 1));
                                    } else if (dealerInfo.getSaleScope() == 1) { //售本省
                                        add(provinceSeriesSpecPrice, currentProvinceId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 1));
                                    } else if (dealerInfo.getSaleScope() == 2) { //售本市
                                    } else if (dealerInfo.getSaleScope() == 3) { //自定义
                                        if (dealerInfo.getSaleCity() != null && dealerInfo.getSaleCity().size() > 0) {
                                            for (Integer saleCity : dealerInfo.getRealSaleCitys()) {
                                                int buzuType = getBuzuType(saleCity, cityId);  //这里可能有跨省补足的数据
                                                add(citySeiesSpecPrice, saleCity, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), buzuType));
                                            }
                                        }
                                        if (dealerInfo.getSaleProvince() != null && dealerInfo.getSaleProvince().size() > 0) {
                                            for (Integer saleProvinceId : dealerInfo.getSaleProvince()) {
                                                if (saleProvinceId == currentProvinceId) {
                                                    add(provinceSeriesSpecPrice, saleProvinceId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 1));
                                                } else {
                                                    add(otherProvinceSeriesSpecPrice, saleProvinceId, spec.getSeriesId(), specId, new DealerAndPrice(dealerInfo.getDealerId(), dealerInfo.getCityId(), price, dealerInfo.getSaleScope(), dealerInfo.getSaleProvince(), 2));
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                xxlLog.accept(specId + " specinfo error" + ExceptionUtil.getStackTrace(e));
                            }
                        }
                    }).exceptionally(e -> {
                        xxlLog.accept("getSpecPriceListByDealerId error:" + ExceptionUtil.getStackTrace(e));
                        return null;
                    }));
                    if (tasks.size() > 30) {
                        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
                        tasks.clear();
                    }
                }
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
            }).exceptionally(e -> {
                xxlLog.accept(ExceptionUtil.getStackTrace(e));
                return null;
            }).join();
        }

        //在售车补足逻辑处理
        clearSpecData(citySeiesSpecPrice, provinceSeriesSpecPrice, otherProvinceSeriesSpecPrice, countrySeriesSpecPrice);
        //停售车补足逻辑处理
        clearSpecData(citySeiesSpecPriceStopSale, provinceSeriesSpecPriceStopSale, otherProvinceSeriesSpecPriceStopSale, countrySeriesSpecPriceStopSale);


        HashSet<String> specNewKeys = new HashSet<>();
        HashSet<String> dealerNewKeys = new HashSet<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        List<CompletableFuture> tasks = new ArrayList<>();
        //保存车型信息
        saveSpec(xxlLog, tasks, specNewKeys, threadPool, citySeiesSpecPrice, provinceSeriesSpecPrice, otherProvinceSeriesSpecPrice, countrySeriesSpecPrice);
        //保存停售车型信息
        saveSpec(xxlLog, tasks, specNewKeys, threadPool, citySeiesSpecPriceStopSale, provinceSeriesSpecPriceStopSale, otherProvinceSeriesSpecPriceStopSale, countrySeriesSpecPriceStopSale);
        CompletableFuture specDeleteTask = CompletableFuture.runAsync(() -> {
            specCityAskPriceComponent.deleteHistorys(specNewKeys, xxlLog);
        });
        //保存经销商车型报价列表
        CompletableFuture dealerDeleteTask = CompletableFuture.runAsync(() -> {
            saveDealerSpecPrice(xxlLog, dealerNewKeys, threadPool, dealerSpecPriceList);
//            dealerSpecPriceComponent.deleteHistorys(dealerNewKeys, xxlLog);
        });
        //用省和全国的车系数据补充城市数据
        citySeiesSpecPrice.forEach((cityId, seriesSpecPrice) -> {
            int provinceId = CityUtil.getProvinceId(cityId);
            ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>> provinceSeriesPrice = provinceSeriesSpecPrice.get(provinceId);
            if (provinceSeriesPrice != null) {
                provinceSeriesPrice.forEach((seriesId, specPrice) -> {
                    ConcurrentMap<Integer, DealerAndPrice> citySpecPrice = seriesSpecPrice.computeIfAbsent(seriesId, key -> new ConcurrentHashMap<>());
                    specPrice.forEach((specId, price) -> {
                        // 没有 || 补足是外省  || 补足是本省且价格大
                        citySpecPrice.compute(specId, (ok, ov) -> ov == null || ov.getBuzuType() == 2 || (ov.getBuzuType() == 1 && ov.getPrice() > price.getPrice()) ? price : ov);
                    });
                });
            }

            //跨省补足（跨省数据已经用全国数据补充过一部分了）
            ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>> otherProvinceSeriesPrice = otherProvinceSeriesSpecPrice.get(provinceId);
            if (otherProvinceSeriesPrice != null) {
                otherProvinceSeriesPrice.forEach((seriesId, specPrice) -> {
                    ConcurrentMap<Integer, DealerAndPrice> citySpecPrice = seriesSpecPrice.computeIfAbsent(seriesId, key -> new ConcurrentHashMap<>());
                    specPrice.forEach((specId, price) -> {
                        // 没有 || 补足是跨省且价格大
                        citySpecPrice.compute(specId, (ok, ov) -> ov == null || (ov.getBuzuType() == 2 && ov.getPrice() > price.getPrice()) ? price : ov);
                    });
                });
            }

            countrySeriesSpecPrice.forEach((seriesId, specPrice) -> {
                ConcurrentMap<Integer, DealerAndPrice> citySpecPrice = seriesSpecPrice.computeIfAbsent(seriesId, key -> new ConcurrentHashMap<>());
                specPrice.forEach((specId, price) -> {
                    // 没有 || 补足是跨省且价格大
                    citySpecPrice.compute(specId, (ok, ov) -> ov == null || (ov.getBuzuType() == 2 && ov.getPrice() > price.getPrice()) ? price : ov);
                });
            });
        });

        HashSet<String> newKeys = new HashSet<>();
        xxlLog.accept("各市车系 start");
        citySeiesSpecPrice.forEach((cityId, seriesPrice) -> {
            tasks.add(CompletableFuture.runAsync(() -> {
                Map<TreeMap<String, Object>, SeriesCityAskPriceDto> datas = new LinkedHashMap<>();
                seriesPrice.forEach((seriesId, specPrice) -> {
                    if (specPrice == null || specPrice.size() == 0) return;
                    DealerAndPrice minPrice = specPrice.values().stream().min(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    DealerAndPrice maxPrice = specPrice.values().stream().max(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    if (minPrice == null || maxPrice == null) {
                        return;
                    }
                    //在售车型的经销商价格
                    List<DealerAndPrice> onSaleSpecPriceList = new ArrayList<>();
                    SeriesSpecDto seriesSpecDto = seriesSpecComponent.get(seriesId);
                    if (seriesSpecDto != null && ListUtil.isNotEmpty(seriesSpecDto.getItems())) {
                        List<SeriesSpecDto.Item> onSaleSpecList = seriesSpecDto.getItems().stream().filter(x -> x.getState() == 20 || x.getState() == 30).collect(Collectors.toList());
                        onSaleSpecList.forEach(spec -> {
                            onSaleSpecPriceList.add(specPrice.get(spec.getId()));
                        });
                    }
                    onSaleSpecPriceList.removeIf(x -> x == null);
                    DealerAndPrice minOnSalePrice = onSaleSpecPriceList.stream().min(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    DealerAndPrice maxOnSalePrice = onSaleSpecPriceList.stream().max(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    datas.put(makeParam(seriesId, cityId), new SeriesCityAskPriceDto() {{
                        setCityId(cityId);
                        setSeriesId(seriesId);
                        setMaxPrice(maxPrice.getPrice());
                        setMinPrice(minPrice.getPrice());
                        setMaxPriceDealer(maxPrice.getDealerId());
                        setMinPriceDealer(minPrice.getDealerId());
                        setCityLocal(minPrice.getBuzuType() == 0 || minPrice.getLocalCity().contains(cityId));
                        setLocalCity(minPrice.getLocalCity());
                        //该报价是否为售本市的经销商
                        if (Objects.nonNull(citySeriesPriceType.get(cityId))
                                && Objects.nonNull(citySeriesPriceType.get(cityId).get(seriesId))) {
                            setPriceType(citySeriesPriceType.get(cityId).get(seriesId));
                        } else if (Objects.nonNull(citySeriesPriceTypeStopSale.get(cityId))
                                && Objects.nonNull(citySeriesPriceTypeStopSale.get(cityId).get(seriesId))) {
                            setPriceType(citySeriesPriceTypeStopSale.get(cityId).get(seriesId));
                        }
                        //在售车型的最低和最高价格
                        if (minOnSalePrice != null && maxOnSalePrice != null) {
                            setMinPriceOnSale(minOnSalePrice.getPrice());
                            setMaxPriceOnSale(maxOnSalePrice.getPrice());
                        }
                    }});
                    if (datas.size() > 300) {
                        newKeys.addAll(datas.keySet().stream().map(x -> getKey(x)).collect(Collectors.toSet()));
                        updateBatch(datas);
                        datas.clear();
                    }
                });
                newKeys.addAll(datas.keySet().stream().map(x -> getKey(x)).collect(Collectors.toSet()));
                updateBatch(datas);
                datas.clear();
            }, threadPool));
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        tasks.clear();
        xxlLog.accept("各市车系 end");

        xxlLog.accept("省车系的最低价 start");
        //更新各省各车型最低价
        provinceSeriesSpecPrice.forEach((provinceId, seriesPrice) -> {
            tasks.add(CompletableFuture.runAsync(() -> {
                Map<TreeMap<String, Object>, SeriesCityAskPriceDto> datas = new LinkedHashMap<>();
                seriesPrice.forEach((seriesId, specPrice) -> {
                    DealerAndPrice minPrice = specPrice.values().stream().min(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    DealerAndPrice maxPrice = specPrice.values().stream().max(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    if (specPrice == null || specPrice.size() == 0) {
                        return;
                    }
                    //在售车型的经销商价格
                    List<DealerAndPrice> onSaleSpecPriceList = new ArrayList<>();
                    SeriesSpecDto seriesSpecDto = seriesSpecComponent.get(seriesId);
                    if (seriesSpecDto != null && ListUtil.isNotEmpty(seriesSpecDto.getItems())) {
                        List<SeriesSpecDto.Item> onSaleSpecList = seriesSpecDto.getItems().stream().filter(x -> x.getState() == 20 || x.getState() == 30).collect(Collectors.toList());
                        onSaleSpecList.forEach(spec -> {
                            onSaleSpecPriceList.add(specPrice.get(spec.getId()));
                        });
                    }
                    onSaleSpecPriceList.removeIf(x -> x == null);
                    DealerAndPrice minOnSalePrice = onSaleSpecPriceList.stream().min(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    DealerAndPrice maxOnSalePrice = onSaleSpecPriceList.stream().max(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    datas.put(makeParam(seriesId, provinceId), new SeriesCityAskPriceDto() {{
                        setCityId(provinceId);
                        setSeriesId(seriesId);
                        setMaxPrice(maxPrice.getPrice());
                        setMinPrice(minPrice.getPrice());
                        setMaxPriceDealer(maxPrice.getDealerId());
                        setMinPriceDealer(minPrice.getDealerId());
                        setLocalCity(minPrice.getLocalCity());
                        //补充对于该车系哪些城市有本地售卖的经销商
                        List<Integer> saleCityList = new ArrayList<>();
                        List<Integer> cityIdList = citySeriesPriceType.keySet().stream()
                                .filter(e -> CityUtil.getProvinceId(e) == provinceId)
                                .toList();
                        cityIdList.forEach(e -> {
                            if (citySeriesPriceType.get(e).containsKey(seriesId)) {
                                saleCityList.add(e);
                            }
                        });
                        List<Integer> cityIdListStopSale = citySeiesSpecPriceStopSale.keySet().stream()
                                .filter(e -> CityUtil.getProvinceId(e) == provinceId)
                                .toList();
                        cityIdListStopSale.forEach(e -> {
                            if (citySeiesSpecPriceStopSale.get(e).containsKey(seriesId)) {
                                saleCityList.add(e);
                            }
                        });
                        setSaleCityList(saleCityList.stream().distinct().toList());
                        //在售车型的最低和最高价格
                        if (minOnSalePrice != null && maxOnSalePrice != null) {
                            setMinPriceOnSale(minOnSalePrice.getPrice());
                            setMaxPriceOnSale(maxOnSalePrice.getPrice());
                        }
                    }});
                    if (datas.size() > 300) {
                        newKeys.addAll(datas.keySet().stream().map(x -> getKey(x)).collect(Collectors.toSet()));
                        updateBatch(datas);
                        datas.clear();
                    }
                });
                newKeys.addAll(datas.keySet().stream().map(x -> getKey(x)).collect(Collectors.toSet()));
                updateBatch(datas);
                datas.clear();
            }, threadPool));
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        tasks.clear();
        xxlLog.accept("省车系的最低价 end");

        xxlLog.accept("跨省各车系 start");
        otherProvinceSeriesSpecPrice.forEach((provinceId, seriesPrice) -> {
            tasks.add(CompletableFuture.runAsync(() -> {
                Map<TreeMap<String, Object>, SeriesCityAskPriceDto> datas = new LinkedHashMap<>();
                seriesPrice.forEach((seriesId, specPrice) -> {
                    DealerAndPrice minPrice = specPrice.values().stream().min(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    DealerAndPrice maxPrice = specPrice.values().stream().max(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    if (specPrice == null || specPrice.size() == 0) {
                        return;
                    }
                    //在售车型的经销商价格
                    List<DealerAndPrice> onSaleSpecPriceList = new ArrayList<>();
                    SeriesSpecDto seriesSpecDto = seriesSpecComponent.get(seriesId);
                    if (seriesSpecDto != null && ListUtil.isNotEmpty(seriesSpecDto.getItems())) {
                        List<SeriesSpecDto.Item> onSaleSpecList = seriesSpecDto.getItems().stream().filter(x -> x.getState() == 20 || x.getState() == 30).collect(Collectors.toList());
                        onSaleSpecList.forEach(spec -> {
                            onSaleSpecPriceList.add(specPrice.get(spec.getId()));
                        });
                    }
                    onSaleSpecPriceList.removeIf(x -> x == null);
                    DealerAndPrice minOnSalePrice = onSaleSpecPriceList.stream().min(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    DealerAndPrice maxOnSalePrice = onSaleSpecPriceList.stream().max(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
                    //为了和本省区分，跨省这里的id*100了，取的时候，需要也*100
                    datas.put(makeParam(seriesId, provinceId * 100), new SeriesCityAskPriceDto() {{
                        setCityId(provinceId);
                        setSeriesId(seriesId);
                        setMaxPrice(maxPrice.getPrice());
                        setMinPrice(minPrice.getPrice());
                        setMaxPriceDealer(maxPrice.getDealerId());
                        setMinPriceDealer(minPrice.getDealerId());
                        //在售车型的最低和最高价格
                        if (minOnSalePrice != null && maxOnSalePrice != null) {
                            setMinPriceOnSale(minOnSalePrice.getPrice());
                            setMaxPriceOnSale(maxOnSalePrice.getPrice());
                        }
                    }});
                    if (datas.size() > 300) {
                        newKeys.addAll(datas.keySet().stream().map(x -> getKey(x)).collect(Collectors.toSet()));
                        updateBatch(datas);
                        datas.clear();
                    }
                });
                newKeys.addAll(datas.keySet().stream().map(x -> getKey(x)).collect(Collectors.toSet()));
                updateBatch(datas);
                datas.clear();
            }, threadPool));
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        tasks.clear();
        xxlLog.accept("跨省各车系 end");


        xxlLog.accept("全国各车系 start");
        Map<TreeMap<String, Object>, SeriesCityAskPriceDto> datas = new LinkedHashMap<>();
        //更新全国各车型最低价
        countrySeriesSpecPrice.forEach((seriesId, specPrice) -> {
            if (specPrice == null || specPrice.size() == 0) {
                return;
            }
            DealerAndPrice minPrice = specPrice.values().stream().min(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
            DealerAndPrice maxPrice = specPrice.values().stream().max(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
            //在售车型的经销商价格
            List<DealerAndPrice> onSaleSpecPriceList = new ArrayList<>();
            SeriesSpecDto seriesSpecDto = seriesSpecComponent.get(seriesId);
            if (seriesSpecDto != null && ListUtil.isNotEmpty(seriesSpecDto.getItems())) {
                List<SeriesSpecDto.Item> onSaleSpecList = seriesSpecDto.getItems().stream().filter(x -> x.getState() == 20 || x.getState() == 30).collect(Collectors.toList());
                onSaleSpecList.forEach(spec -> {
                    onSaleSpecPriceList.add(specPrice.get(spec.getId()));
                });
            }
            onSaleSpecPriceList.removeIf(x -> x == null);
            DealerAndPrice minOnSalePrice = onSaleSpecPriceList.stream().min(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
            DealerAndPrice maxOnSalePrice = onSaleSpecPriceList.stream().max(Comparator.comparing(DealerAndPrice::getPrice)).orElse(null);
            datas.put(makeParam(seriesId, countryId), new SeriesCityAskPriceDto() {{
                setCityId(countryId);
                setSeriesId(seriesId);
                setMaxPrice(maxPrice.getPrice());
                setMinPrice(minPrice.getPrice());
                setMaxPriceDealer(maxPrice.getDealerId());
                setMinPriceDealer(minPrice.getDealerId());
                setLocalCity(minPrice.getLocalCity());
                //在售车型的最低和最高价格
                if (minOnSalePrice != null && maxOnSalePrice != null) {
                    setMinPriceOnSale(minOnSalePrice.getPrice());
                    setMaxPriceOnSale(maxOnSalePrice.getPrice());
                }
            }});
            if (datas.size() > 300) {
                newKeys.addAll(datas.keySet().stream().map(x -> getKey(x)).collect(Collectors.toSet()));
                updateBatch(datas);
                datas.clear();
            }
        });
        newKeys.addAll(datas.keySet().stream().map(x -> getKey(x)).collect(Collectors.toSet()));
        updateBatch(datas);
        datas.clear();
        xxlLog.accept("全国各车系 end");

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        deleteHistory(newKeys, xxlLog);

        specDeleteTask.join();
        dealerDeleteTask.join();

        xxlLog.accept("success all");
    }


    void clearSpecData(
            ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> citySeiesSpecPrice,
            ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> provinceSeriesSpecPrice,
            ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> otherProvinceSeriesSpecPrice,
            ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>> countrySeriesSpecPrice
    ) {

        /**
         * 反向处理城市中的跨省补足
         * 如果城市中的价格是跨省补足的，并且本省有价格的话，就删除掉
         * 如果城市中的价格是跨省补足的，本省没有价格，但是全国有价格，并且比城市中的价格低，也删除掉
         */
        citySeiesSpecPrice.forEach((cityId, seriesSpecPrice) -> {
            seriesSpecPrice.forEach((seriesId, specPrice) -> {
                ConcurrentMap<Integer, DealerAndPrice> pSpecPrice = provinceSeriesSpecPrice.get(CityUtil.getProvinceId(cityId)).get(seriesId);
                if (pSpecPrice != null && pSpecPrice.size() > 0) {
                    pSpecPrice.forEach((specId, price) -> {
                        if (specPrice.containsKey(specId)) {
                            if (specPrice.get(specId).getBuzuType() == 2 || (specPrice.get(specId).getBuzuType() == 1 && specPrice.get(specId).getPrice() >= price.getPrice())) {
                                specPrice.remove(specId);
                            }
                        }
                    });
                }
                ConcurrentMap<Integer, DealerAndPrice> cSpecPrice = countrySeriesSpecPrice.get(seriesId);
                if (cSpecPrice != null && cSpecPrice.size() > 0) {
                    cSpecPrice.forEach((specId, price) -> {
                        if (specPrice.containsKey(specId) && specPrice.get(specId).getBuzuType() == 2 && specPrice.get(specId).getPrice() > price.getPrice()) {
                            specPrice.remove(specId);
                        }
                    });
                }
            });
        });

        //如果全国的补足数据比跨省的补足数据价格低，就删除跨省的补足数据
        otherProvinceSeriesSpecPrice.forEach((provinceId, seriesPrice) -> {
            seriesPrice.forEach((seriesId, specPrice) -> {
                if (!countrySeriesSpecPrice.containsKey(seriesId)) {
                    return;
                }
                countrySeriesSpecPrice.get(seriesId).forEach((specId, price) -> {
                    if (specPrice.containsKey(specId) && specPrice.get(specId).getPrice() >= price.getPrice()) {
                        specPrice.remove(specId);
                    }
                });
            });
        });

        //如果省内最低价与全国最低价一致，只保留全国最低价
        provinceSeriesSpecPrice.forEach((provinceId, seriesPrice) -> {
            seriesPrice.forEach((seriesId, specPrice) -> {
                if (!countrySeriesSpecPrice.containsKey(seriesId)) {
                    return;
                }
                countrySeriesSpecPrice.get(seriesId).forEach((specId, cspecPrice) -> {
                    if (!specPrice.containsKey(specId)) {
                        return;
                    }
                    if (specPrice.get(specId).getPrice() == cspecPrice.getPrice()) {
                        specPrice.remove(specId);
                    }
                });

            });
        });

        //市的如果与本省的最低价一致，只保留本省的
        //如果本省没有，则对比全国的
        citySeiesSpecPrice.forEach((cityId, seriesPrice) -> {
            int provinceId = CityUtil.getProvinceId(cityId);
            ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>> pss = provinceSeriesSpecPrice.containsKey(provinceId) ? provinceSeriesSpecPrice.get(provinceId) : null;
            seriesPrice.forEach((seriesId, specPrice) -> {
                HashSet<Integer> ncids = new HashSet<>();
                if (pss != null) {
                    pss.forEach((pSeriesId, pSeriesPrice) -> {
                        pSeriesPrice.forEach((specId, pSpecPrice) -> {
                            if (!specPrice.containsKey(specId)) {
                                ncids.add(specId);
                                return;
                            }
                            DealerAndPrice item = specPrice.get(specId);
                            if (item.getPrice() == pSpecPrice.getPrice()) {
                                if (item.getBuzuType() == 0) {
                                    pSpecPrice.getLocalCity().add(cityId);
                                }
                                specPrice.remove(specId);
                            }
                        });
                    });
                }
                if (countrySeriesSpecPrice.containsKey(seriesId)) {
                    countrySeriesSpecPrice.get(seriesId).forEach((specId, cspecPrice) -> {
                        if (!ncids.contains(specId)) {
                            return;
                        }
                        if (!specPrice.containsKey(specId)) {
                            return;
                        }
                        DealerAndPrice item = specPrice.get(specId);
                        if (item.getPrice() == cspecPrice.getPrice()) {
                            if (item.getBuzuType() == 0) {
                                cspecPrice.getLocalCity().add(cityId);
                            }
                            specPrice.remove(specId);
                        }
                    });
                }
            });
        });
    }

    void saveSpec(
            Consumer<String> xxlLog,
            List<CompletableFuture> tasks,
            HashSet<String> specNewKeys,
            ExecutorService threadPool,
            ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> citySeiesSpecPrice,
            ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> provinceSeriesSpecPrice,
            ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>>> otherProvinceSeriesSpecPrice,
            ConcurrentMap<Integer, ConcurrentMap<Integer, DealerAndPrice>> countrySeriesSpecPrice
    ) {

        xxlLog.accept("更新各市车型最低价 start");
        citySeiesSpecPrice.forEach((cityId, seriesPrice) -> {
            tasks.add(CompletableFuture.runAsync(() -> {
                CopyOnWriteArrayList<SpecCityAskPriceComponent.ParamItem> paramItemList = new CopyOnWriteArrayList<>();
                seriesPrice.forEach((seriesId, specPrice) -> {
                    specPrice.forEach((specId, price) -> {
                        paramItemList.add(new SpecCityAskPriceComponent.ParamItem(specId, cityId, price.getPrice(), price.getDealerId(), price.getDealerCityId(), price.getLocalCity()));
                        if (paramItemList.size() > 300) {
                            specNewKeys.addAll(paramItemList.stream().map(x -> specCityAskPriceComponent.getKey(x.makeParam())).collect(Collectors.toSet()));
                            specCityAskPriceComponent.updateBeatch(paramItemList);
                            paramItemList.clear();
                        }
                    });
                });
                specNewKeys.addAll(paramItemList.stream().map(x -> specCityAskPriceComponent.getKey(x.makeParam())).collect(Collectors.toSet()));
                specCityAskPriceComponent.updateBeatch(paramItemList);
                paramItemList.clear();
            }, threadPool));
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        tasks.clear();
        xxlLog.accept("更新各市车型最低价 end");


        xxlLog.accept("更新各省各车型最低价 start");
        provinceSeriesSpecPrice.forEach((provinceId, seriesPrice) -> {
            tasks.add(CompletableFuture.runAsync(() -> {
                CopyOnWriteArrayList<SpecCityAskPriceComponent.ParamItem> paramItemList = new CopyOnWriteArrayList<>();
                seriesPrice.forEach((seriesId, specPrice) -> {
                    specPrice.forEach((specId, price) -> {
                        paramItemList.add(new SpecCityAskPriceComponent.ParamItem(specId, provinceId, price.getPrice(), price.getDealerId(), price.getDealerCityId(), price.getLocalCity()));
                        if (paramItemList.size() > 300) {
                            specNewKeys.addAll(paramItemList.stream().map(x -> specCityAskPriceComponent.getKey(x.makeParam())).collect(Collectors.toSet()));
                            specCityAskPriceComponent.updateBeatch(paramItemList);
                            paramItemList.clear();
                        }
                    });
                });
                specNewKeys.addAll(paramItemList.stream().map(x -> specCityAskPriceComponent.getKey(x.makeParam())).collect(Collectors.toSet()));
                specCityAskPriceComponent.updateBeatch(paramItemList);
                paramItemList.clear();
            }, threadPool));
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        tasks.clear();
        xxlLog.accept("更新各省各车型最低价 end");


        xxlLog.accept("更新跨省各车型最低价 start");
        //更新跨省各车型最低价
        otherProvinceSeriesSpecPrice.forEach((provinceId, seriesPrice) -> {
            tasks.add(CompletableFuture.runAsync(() -> {
                CopyOnWriteArrayList<SpecCityAskPriceComponent.ParamItem> paramItemList = new CopyOnWriteArrayList<>();
                seriesPrice.forEach((seriesId, specPrice) -> {
                    specPrice.forEach((specId, price) -> {
                        //为了和本省区分，跨省这里的id*100了，取的时候，需要也*100
                        paramItemList.add(new SpecCityAskPriceComponent.ParamItem(specId, provinceId * 100, price.getPrice(), price.getDealerId(), price.getDealerCityId(), price.getLocalCity()));
                        if (paramItemList.size() > 300) {
                            specNewKeys.addAll(paramItemList.stream().map(x -> specCityAskPriceComponent.getKey(x.makeParam())).collect(Collectors.toSet()));
                            specCityAskPriceComponent.updateBeatch(paramItemList);
                            paramItemList.clear();
                        }
                    });
                });
                specNewKeys.addAll(paramItemList.stream().map(x -> specCityAskPriceComponent.getKey(x.makeParam())).collect(Collectors.toSet()));
                specCityAskPriceComponent.updateBeatch(paramItemList);
                paramItemList.clear();
            }, threadPool));
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        tasks.clear();
        xxlLog.accept("更新跨省各车型最低价 end");

        xxlLog.accept("更新全国各车型最低价 start");
        countrySeriesSpecPrice.forEach((seriesId, specPrice) -> {
            tasks.add(CompletableFuture.runAsync(() -> {
                CopyOnWriteArrayList<SpecCityAskPriceComponent.ParamItem> paramItemList = new CopyOnWriteArrayList<>();
                specPrice.forEach((specId, price) -> {
                    paramItemList.add(new SpecCityAskPriceComponent.ParamItem(specId, countryId, price.getPrice(), price.getDealerId(), price.getDealerCityId(), price.getLocalCity()));
                    if (paramItemList.size() > 300) {
                        specNewKeys.addAll(paramItemList.stream().map(x -> specCityAskPriceComponent.getKey(x.makeParam())).collect(Collectors.toSet()));
                        specCityAskPriceComponent.updateBeatch(paramItemList);
                        paramItemList.clear();
                    }
                });
                specNewKeys.addAll(paramItemList.stream().map(x -> specCityAskPriceComponent.getKey(x.makeParam())).collect(Collectors.toSet()));
                specCityAskPriceComponent.updateBeatch(paramItemList);
                paramItemList.clear();
            }, threadPool));
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        tasks.clear();
        xxlLog.accept("更新全国各车型最低价 end");

    }


    void saveDealerSpecPrice(Consumer<String> xxlLog,
                             HashSet<String> dealerNewKeys,
                             ExecutorService threadPool,
                             List<DealerSpecPriceListResult> dealerList) {
        xxlLog.accept("更新经销商车型报价列表 start");
        List<CompletableFuture> tasks = new ArrayList<>();
        Lists.partition(dealerList, 300).forEach(list -> {
            tasks.add(CompletableFuture.runAsync(() -> {
                dealerNewKeys.addAll(list.stream().map(x -> dealerSpecPriceComponent.getKey(x.getDealerId())).collect(Collectors.toSet()));
                dealerSpecPriceComponent.updateBeatch(list);
            }, threadPool));
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        xxlLog.accept("更新经销商车型报价列表 end");
    }

    public CompletableFuture<BaseModel<List<SpecPriceItem>>> getSpecPriceListByDealerId(int dealerId) {
        return dealerApiClient.getSpecPriceListByDealerId(dealerId).thenCompose(result -> {
            if (Objects.isNull(result)) {
                return dealerApiClient.getSpecPriceListByDealerId(dealerId);
            } else {
                return CompletableFuture.completedFuture(result);
            }
        });

    }

    int getBuzuType(int newCity, int oldCity) {
        if (newCity == oldCity) {
            return 0;
        } else if (CityUtil.getProvinceId(newCity) == CityUtil.getProvinceId(oldCity)) {
            return 1;
        } else {
            return 2;
        }
    }

    public List<Integer> getSeriesByCity(int cityId) {
        return seriesCityAskPriceNewMapper.getSeriesIdsByCity(cityId);
    }

    public DealerSpecPriceListResult getDealerSpecPriceDto(ListCshDealerByCityResult dealer, List<SpecPriceItem> priceList) {
        DealerSpecPriceListResult dealerSpecDto = new DealerSpecPriceListResult();
        BeanUtils.copyProperties(dealer, dealerSpecDto);
        priceList.forEach(price -> {
            dealerSpecDto.getSpecList().add(new DealerSpecPriceListResult.SpecPrice(price.getSpecId(), price.getPrice()));
        });
        return dealerSpecDto;
    }

}
