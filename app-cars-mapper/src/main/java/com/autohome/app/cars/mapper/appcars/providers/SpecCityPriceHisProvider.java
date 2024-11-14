package com.autohome.app.cars.mapper.appcars.providers;

/**
 * @author zhangchengtao
 * @date 2024/8/14 20:51
 */
public class SpecCityPriceHisProvider {
    public String selectBySpecIdIn(String specIds) {
        return "SELECT * FROM spec_city_price_his WHERE specId IN (" + specIds + ")";
    }

    public String selectByCityIdAndSpecIdIn(int cityId, String specIds) {
        return "SELECT * FROM spec_city_price_his WHERE cityId = " + cityId + " AND specId IN (" + specIds + ")";
    }
}
