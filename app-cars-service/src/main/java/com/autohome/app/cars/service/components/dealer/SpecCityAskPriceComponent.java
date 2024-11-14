package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.hangqing.dtos.CitySpecPriceHangqingDto;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Component
@DBConfig(tableName = "spec_city_askprice")
@RedisConfig(keyVersion = "v4")
public class SpecCityAskPriceComponent extends BaseComponent<SpecCityAskPriceDto> {

    @Autowired
    DealerApiClient dealerApiClient;

    @Autowired
    SpecMapper specMapper;

    final static String specIdIdParamName = "specId";
    final static String cityParamName = "cityId";

    final static int countryId = 0;

    TreeMap<String, Object> makeParam(int specId, int cityId) {
        return ParamBuilder.create(specIdIdParamName, specId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<List<SpecCityAskPriceDto>> get(List<Integer> specIds, Integer cityId) {
        List<CompletableFuture> tasks = new ArrayList<>();
        List<SpecCityAskPriceDto> dtoList = Collections.synchronizedList(new ArrayList<>());
        specIds.forEach(specId -> tasks.add(get(specId, cityId).thenApply(dtoList::add)));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        return CompletableFuture.completedFuture(dtoList);
    }

    public CompletableFuture<List<SpecCityAskPriceDto>> getListAsync(List<Integer> specIdList, Integer cityId) {
        List<CompletableFuture> tasks = new ArrayList<>();
        List<SpecCityAskPriceDto> dtoList = Collections.synchronizedList(new ArrayList<>());
        specIdList.forEach(specId -> tasks.add(get(specId, cityId).thenApply(dtoList::add)));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        return CompletableFuture.completedFuture(dtoList);
    }

    public String get(TreeMap<String, Object> params) {
        SpecCityAskPriceDto dto = get((int) params.get("specId"),
                (int) params.get("cityId")).join();
        return JsonUtil.toString(dto);
    }

    public CompletableFuture<SpecCityAskPriceDto> get(int specId, int cityId) {
        if(CityUtil.isGangAoTaiCity(cityId)){
            return CompletableFuture.completedFuture(null);
        }
        return baseGetAsync(makeParam(specId, cityId)).thenCompose(cityInfo->{
            if(cityInfo!=null){
                return CompletableFuture.completedFuture(cityInfo);
            }
            int provinceId = CityUtil.getProvinceId(cityId);
            return baseGetAsync(makeParam(specId,provinceId)).thenCompose(provinceInfo->{
                if(provinceInfo!=null){
                    return CompletableFuture.completedFuture(provinceInfo);
                }
               return baseGetAsync(makeParam(specId,countryId)).thenCombine(baseGetAsync(makeParam(specId,provinceId*100)),(country,province)->{
                   if(country==null && province==null){
                       return null;
                   }else if(country==null){
                       return province;
                   }else if(province==null){
                       return country;
                   }else{
                       return province.getMinPrice()<country.getMinPrice()?province:country;
                   }
               });
            });
        });
    }

    public void updateBeatch(List<ParamItem> items) {
        Map<TreeMap<String,Object>,SpecCityAskPriceDto> datas = new HashMap<>();
        for (ParamItem item : items) {
            SpecCityAskPriceDto dto = new SpecCityAskPriceDto();
            dto.setCityId(item.getCityId());
            dto.setSpecId(item.getSpecId());
            dto.setMinPrice(item.getMinPrice());
            dto.setMinPriceDealer(item.getMinPriceDealer());
            dto.setMinPriceCityId(item.getMinPriceCityId());
            dto.setMinPriceCityIdList(item.getMinPriceCityIdList());
            datas.put(makeParam(item.getSpecId(), item.getCityId()),dto);
        }
        updateBatch(datas);
    }

    public void deleteHistorys(HashSet<String> newKeys,Consumer<String> xxlLog){
        deleteHistory(newKeys,xxlLog);
    }

    @Data
    public static class ParamItem {
        public ParamItem(int specId, int cityId, int minPrice, int minPriceDealer, int minPriceCityId, HashSet<Integer> minPriceCityIdList) {
            setSpecId(specId);
            setCityId(cityId);
            setMinPrice(minPrice);
            setMinPriceDealer(minPriceDealer);
            setMinPriceCityId(minPriceCityId);
            setMinPriceCityIdList(minPriceCityIdList);
        }

        int specId;
        int cityId;
        int minPriceDealer;
        int minPrice;
        int minPriceCityId;
        HashSet<Integer> minPriceCityIdList;

        public TreeMap<String, Object> makeParam() {
            return ParamBuilder.create(specIdIdParamName, getSpecId()).add(cityParamName, getCityId()).build();
        }

    }

}
