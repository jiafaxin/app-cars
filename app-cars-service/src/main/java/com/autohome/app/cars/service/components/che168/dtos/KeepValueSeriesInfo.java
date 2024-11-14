package com.autohome.app.cars.service.components.che168.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class KeepValueSeriesInfo {
    /**
     * 车系id
     */
    private int seriesId;
    /**
     * 排行值
     */
    private Integer followrank;

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
