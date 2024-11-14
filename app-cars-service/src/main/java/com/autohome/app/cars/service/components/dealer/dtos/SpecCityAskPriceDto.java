package com.autohome.app.cars.service.components.dealer.dtos;

import lombok.Data;

import java.util.HashSet;

@Data
public class SpecCityAskPriceDto {
    int specId;
    int cityId;
    //所有车车型最低售价
    int minPriceDealer;
    int minPrice;
    int minPriceCityId;
    HashSet<Integer> minPriceCityIdList;
}
