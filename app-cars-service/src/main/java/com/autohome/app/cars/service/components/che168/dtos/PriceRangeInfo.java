package com.autohome.app.cars.service.components.che168.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PriceRangeInfo {
    /**
     * 不限年款最低报价(单位：万元)
     */
    private Double minprice;
    /**
     * 不限年款最高报价(单位：万元)
     */
    private Double maxprice;
    /**
     * 不限年款均价(单位：万元)
     */
    private Double avgprice;
    /**
     * 不限年款在售车数
     */
    private int cunt;
    /**
     * 城市id
     */
    private int cityid;

    private List<SeriesYears> seriesyears = new ArrayList<>();

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
