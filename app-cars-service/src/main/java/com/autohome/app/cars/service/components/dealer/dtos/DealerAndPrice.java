package com.autohome.app.cars.service.components.dealer.dtos;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class DealerAndPrice {
    public DealerAndPrice(int dealerId,int dealerCityId,int price,int saleScope,Set<Integer> buzuProvinceIds,int buzuType){
        setDealerId(dealerId);
        setDealerCityId(dealerCityId);
        setPrice(price);
        setSaleScope(saleScope);
        setBuzuType(buzuType);
        setBuzuProvinceIds(buzuProvinceIds);
        setLocalCity(new HashSet<>());
    }
    int dealerId;
    int dealerCityId;
    int price;

    //补足类型，0 不是补足，1省内补足，2全国补足
    int buzuType;

    //补足的省份id，当补足类型=0的时候，才能用
    Set<Integer> buzuProvinceIds;
    int saleScope;
    HashSet<Integer> localCity;


}
