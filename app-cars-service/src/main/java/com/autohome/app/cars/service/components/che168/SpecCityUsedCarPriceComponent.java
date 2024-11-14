package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.che168.dtos.GetCarCityPriceListResult;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerSpecCanAskPriceNewApiResult;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.che168.dtos.SpecCityUsedCarDto;
import com.autohome.app.cars.service.components.che168.dtos.SpecUsedCarPriceInfo;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.subsidy.dtos.SpecCitySubsidyDto;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 何峰
 * 二手车车型分城市报价区间
 */
@Component
@DBConfig(tableName = "spec_city_usedcar_price")
public class SpecCityUsedCarPriceComponent extends BaseComponent<SpecCityUsedCarDto> {
    @Autowired
    Api2scautork2Client api2scautork2Client;
    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Autowired
    SpecMapper specMapper;

    final static String specIdParamName = "specId";
    final static String cityParamName = "cityId";
    final static int countryId = 0;


    TreeMap<String, Object> makeParam(int specId, int cityId) {
        return ParamBuilder.create(specIdParamName, specId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SpecCityUsedCarDto> getCountry(int specId) {
        return baseGetAsync(makeParam(specId,countryId)).thenApply(countryInfo->{
            if(countryInfo!=null){
                return countryInfo;
            }
            return null;
        });
    }

    public CompletableFuture<SpecCityUsedCarDto> get(int specId, int cityId) {
        return baseGetAsync(makeParam(specId, cityId)).thenCompose(cityInfo->{
            if(cityInfo!=null){
                return CompletableFuture.completedFuture(cityInfo);
            }
            int provinceId = CityUtil.getProvinceId(cityId);
            return baseGetAsync(makeParam(specId,provinceId)).thenCompose(provinceInfo->{
                if(provinceInfo!=null){
                    return CompletableFuture.completedFuture(provinceInfo);
                }
                return baseGetAsync(makeParam(specId,countryId)).thenApply(countryInfo->{
                    if(countryInfo!=null){
                        return countryInfo;
                    }
                    return null;
                });
            });
        });
    }

    public CompletableFuture<List<SpecCityUsedCarDto>> getList(List<Integer> specIdList,int cityId) {
        List<CompletableFuture> tasks = new ArrayList<>();
        List<SpecCityUsedCarDto> dtoList = Collections.synchronizedList(new ArrayList<>());
        specIdList.forEach(specId -> tasks.add(get(specId, cityId).thenApply(dtoList::add)));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        return CompletableFuture.completedFuture(dtoList);
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        HashSet<String> newKeys = new HashSet<>();
        // 所有车型
        List<SpecEntity> allSpecEntities = specMapper.getSpecAll();
        allSpecEntities.addAll(specMapper.getCvSpecAll());
        allSpecEntities = allSpecEntities.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Map<Integer, SpecEntity> specMap = allSpecEntities.stream().collect(Collectors.toMap(SpecEntity::getId, Function.identity(), (k1, k2) -> k1));

        loopSpec(totalMinutes, (specId) -> {
            api2scautork2Client.getCarCityPriceList(specId).thenAccept(data -> {
                ConcurrentMap<TreeMap<String, Object>, SpecCityUsedCarDto> cityDatas = new ConcurrentHashMap<>();
                if (Objects.isNull(data)|| data.getReturncode() != 0) {
                    for (Integer cityId : CityUtil.getAllCityIds()) {
                        newKeys.add(getKey(makeParam(specId, cityId)));
                    }
                    return;
                }
                if (Objects.nonNull(data.getResult())) {
                    SpecDetailDto specDto = specDetailComponent.getSync(specId);
                    String specurl="autohome://usedcar/buycarlist?pvareaid=103988&brand=";
                    if(specDto!=null){
                        specurl = specurl+ UrlUtil.encode(String.format("{\"brandid\":\"%s\",\"bname\":\"%s\",\"seriesid\":\"%s\",\"sname\":\"%s\"}",specDto.getBrandId(),specDto.getBrandName(),specDto.getSeriesId(),specDto.getSeriesName()));
                    }
                    List<GetCarCityPriceListResult.CitylistDTO> dataList = new ArrayList<>();
                    dataList.addAll(data.getResult().getCitylist());
                    dataList.addAll(data.getResult().getProvlist());
                    dataList.addAll(data.getResult().getOtherlist());

                    String finalSpecurl = specurl;
                    dataList.forEach(item->{
                        SpecEntity specEntity = specMap.get(item.getSpecid());
                        SpecCityUsedCarDto dto = new SpecCityUsedCarDto();
                        dto.setSpecyearid(specEntity.getYearId());
                        dto.setMinprice(item.getMinprice());
                        dto.setMaxprice(item.getMaxprice());
                        dto.setSpecid(item.getSpecid());
                        dto.setCityid(item.getCityid());
                        dto.setCunt(item.getCunt());
                        dto.setSpecurl(finalSpecurl);
                        cityDatas.put(makeParam(dto.getSpecid(), dto.getCityid()), dto);
                        newKeys.add(getKey(makeParam(dto.getSpecid(), dto.getCityid())));
                    });
                }
                updateBatch(cityDatas);
            }).exceptionally(e -> {
                xxlLog.accept(specId + " - 二手车城市报价列表"  + " 失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
            xxlLog.accept(String.format("车型id[%s] 二手车城市报价 success",specId));
        }, xxlLog);

        deleteHistory(newKeys,xxlLog);
    }
}
