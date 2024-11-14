package com.autohome.app.cars.service.components.hangqing.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/6/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitySortHangqingDto {

    List<HangqingDto> dtoList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HangqingDto {

        int seriesId;

        int brandId;

        int levelId;

        int minPrice;

        int maxPrice;

        int energyType;

        String fuelTypeDetail;

        /**
         * 车型降价信息（仅【近期降价】相关排序策略会有）
         */
        PriceOffInfo priceOffInfo;

        /**
         * 车型降价信息（仅【历史低价】相关排序策略会有）
         */
        PriceHisInfo priceHisInfo;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceOffInfo {

        /**
         * 进入降价列表的日期
         */
        String startDt;

        List<SpecPriceOffInfo> specPriceOffInfoList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SpecPriceOffInfo {

        int specId;

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
         * 车系下所有车型中，最近一次经销商降价日期
         */
        String lastDt;

        List<SpecPriceHisInfo> specPriceHisInfoList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SpecPriceHisInfo {

        int specId;

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
