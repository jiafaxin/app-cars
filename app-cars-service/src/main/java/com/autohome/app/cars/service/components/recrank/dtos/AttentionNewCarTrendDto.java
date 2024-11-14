package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/27
 */
@Data
public class AttentionNewCarTrendDto {

    /**
     * 车系id
     */
    private int seriesId;

    /**
     * 车系名称
     */
    private String seriesName;

    /**
     * 标题文案
     */
    private String title;

    /**
     * 当前排名
     */
    private int currentRankNum;

    private int max;

    private int chartColNum;

    /**
     * 排名趋势数据
     */
    private List<TrendDto> trendList = new ArrayList<>();

    private List<MonthDayDto> month = new ArrayList<>();


    @Data
    public static class TrendDto {


        /**
         * 排名 39名
         */
        private String rank;

        /**
         * 排名 39
         */
        private int rankNum;

        /**
         * 关注度值
         */
        private int attNum;

    }

    @Data
    public static class MonthDayDto {
        /**
         * 日期
         */
        private Date date;

        /**
         * 日期 03/22
         */
        private String dateFormatStr;

    }
}
