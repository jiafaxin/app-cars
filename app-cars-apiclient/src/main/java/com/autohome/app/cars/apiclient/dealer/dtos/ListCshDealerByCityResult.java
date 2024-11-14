package com.autohome.app.cars.apiclient.dealer.dtos;

import com.autohome.app.cars.common.utils.CityUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ListCshDealerByCityResult {
    int dealerId;
    String dealerName;
    BigDecimal  latitude;
    BigDecimal longitude;
    int cityId;
    int saleScope;
    Set<Integer> saleCity;
    Set<Integer> saleProvince;

    public Set<Integer> getRealSaleCitys() {
        if (saleProvince == null || saleProvince.size() == 0 || saleCity==null)
            return saleCity;
        saleCity.removeIf(x -> saleProvince.contains(CityUtil.getProvinceId(x)));
        return saleCity;
    }

}
