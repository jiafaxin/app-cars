package com.autohome.app.cars.mapper.appcars.providers;

import com.autohome.app.cars.mapper.appcars.entities.SpecDealerMinPriceEntity;

import java.util.List;
import java.util.Map;

public class SpecDealerMinPriceProvider {
    public String insertBeatch(Map<String, Object> parameters){
        List<SpecDealerMinPriceEntity> infos = (List<SpecDealerMinPriceEntity>)parameters.get("infos");
        String sqlTemp = "INSERT INTO spec_dealer_min_price (provinceId, cityId, seriesId,specId,minPrice,minPriceDealerId,version)\n" +
                "VALUES (#{provinceId#index},#{cityId#index},#{seriesId#index},#{specId#index},#{minPrice#index},#{minPriceDealerId#index},#{version#index})\n" +
                "ON DUPLICATE KEY UPDATE minPrice=#{minPrice#index},minPriceDealerId=#{minPriceDealerId#index},version=#{version#index};";

        StringBuilder sqls = new StringBuilder();
        for (int i = 0; i < infos.size(); i++) {
            SpecDealerMinPriceEntity info = infos.get(i);
            parameters.put("provinceId"+i,info.getProvinceId());
            parameters.put("cityId"+i,info.getCityId());
            parameters.put("seriesId"+i,info.getSeriesId());
            parameters.put("specId"+i,info.getSpecId());
            parameters.put("minPrice"+i,info.getMinPrice());
            parameters.put("minPriceDealerId"+i,info.getMinPriceDealerId());
            parameters.put("version"+i,info.getVersion());
            sqls.append(sqlTemp.replace("#index",""+i));
        }

        return sqls.toString();
    }
}
