package com.autohome.app.cars.apiclient.che168.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class KeepValueSeriesResult {
    /**
     * 排行值
     */
    private Integer followrank;
    /**
     * 总用车成本
     */
    private Integer totalcost;
    /**
     * 车系名称
     */
    private String seriesname;
    /**
     * 车系级别名称
     */
    private String serieslevel;
    /**
     * 车系图片
     */
    private String carpic;
    /**
     * 车系保值率数据
     */
    private List<KeepRateInfo> serieskeeprate;
    /**
     * 车系级别保值率数据
     */
    private List<KeepRateInfo> levelkeeprate;

    @Data
    public static class KeepRateInfo {
        /**
         * 年，第一年保值率，第二年保值率..
         */
        private int year;
        /**
         * 保值率
         */
        private BigDecimal keeprate;
    }
}
