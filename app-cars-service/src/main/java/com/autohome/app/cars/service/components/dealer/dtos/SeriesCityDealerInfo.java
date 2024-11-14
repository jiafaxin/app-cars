package com.autohome.app.cars.service.components.dealer.dtos;

import lombok.Data;

@Data
public class SeriesCityDealerInfo {
    int seriesId;
    int cityId;

    //4s 保养
    By4s by4s;

    //优惠政策
    PricePolicy pricePolicy;

    @Data
    public static class By4s{
        boolean havedealers;
        String jumpdealerlisturl;
    }

    @Data
    public static class PricePolicy{
        int newCar;
        int replaceCar;
    }
}
