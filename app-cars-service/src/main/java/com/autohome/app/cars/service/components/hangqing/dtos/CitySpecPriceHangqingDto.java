package com.autohome.app.cars.service.components.hangqing.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chengjincheng
 * @date 2024/6/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitySpecPriceHangqingDto {

    int specId;

    /**
     * 当前最低指导价
     */
    int specMinPrice;

    /**
     * 当前最低经销商价
     */
    int dealerMinPrice;

    /**
     * 车型降价信息
     */
    PriceOffInfo priceOffInfo;

    /**
     * 历史新低信息
     */
    PriceHisInfo priceHisInfo;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceOffInfo {

        /**
         * 降价日期
         */
        String dt;

        /**
         * 降价金额
         */
        int priceOff;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceHisInfo {

        /**
         * 车型最近一次经销商降价日期
         */
        String lastDt;

        /**
         * 180天新低/历史新低
         */
        String priceHisTag;
    }
}
