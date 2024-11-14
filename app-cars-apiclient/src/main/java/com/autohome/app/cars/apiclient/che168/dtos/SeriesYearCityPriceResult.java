package com.autohome.app.cars.apiclient.che168.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SeriesYearCityPriceResult {
    /**
     * 城市数据
     */
    private List<PriceInfo> cityinfo;
    /**
     * 省份数据
     */
    private List<PriceInfo> provinfo;
    /**
     * 全国数据
     */
    private List<PriceInfo> otherinfo;


    @Data
    public static class PriceInfo {
        /**
         * 不限年款最低报价(单位：万元)
         */
        private double minprice;
        /**
         * 不限年款最高报价(单位：万元)
         */
        private double maxprice;
        /**
         * 不限年款均价(单位：万元)
         */
        private double avgprice;
        /**
         * 不限年款在售车数
         */
        private int cunt;
        /**
         * 城市id
         */
        private int cityid;

        private List<SeriesYears> seriesyears;
    }

    @Data
    public static class SeriesYears {
        /**
         * 年款id
         */
        private Integer seriesyearid;
        /**
         * 年款名称，例如：2024
         */
        private String yearname;
        /**
         * 城市
         */
        private Integer cityid;
        /**
         * 最低报价(单位：万元)
         */
        private BigDecimal minprice;
        /**
         * 最高报价(单位：万元)
         */
        private BigDecimal maxprice;
        /**
         * 均价(单位：万元)
         */
        private BigDecimal avgprice;
        /**
         * 在售车数
         */
        private int cunt;
    }
}
