package com.autohome.app.cars.mapper.appcars.entities;

import lombok.Data;

@Data
public class SpecDealerMinPriceEntity {
    public SpecDealerMinPriceEntity(
            int provinceId,
            int cityId,
            int seriesId,
            int specId,
            int minPrice,
            int minPriceDealerId,
            String version) {
        setProvinceId(provinceId);
        setCityId(cityId);
        setMinPrice(minPrice);
        setMinPriceDealerId(minPriceDealerId);
        setSpecId(specId);
        setSeriesId(seriesId);
        setVersion(version);
    }

    int provinceId;
    int cityId;
    int seriesId;
    int specId;
    int minPrice;
    int minPriceDealerId;
    String version;
}
