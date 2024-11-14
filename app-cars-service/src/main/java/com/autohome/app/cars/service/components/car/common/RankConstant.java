package com.autohome.app.cars.service.components.car.common;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

/**
 * 销量榜公共参数
 * @author zhangchengtao
 * @date 2024/4/28 14:42
 */
public class RankConstant {

    /**
     * 在售状态
     */
    public static final List<Integer> ON_SALE_STATE_LIST = Arrays.asList(20, 30);

    public static final String MONTH_DATE_PATTERN = "yyyy-MM";
    public static final String WEEK_DATE_PATTERN = "yyyy-MM-dd";
    public static final String WEEK_RANGE_DATE_PATTERN = "MM/dd";
    /**
     * 月份日期Format
     */
    public static final SimpleDateFormat MONTH_DATE_FORMAT = new SimpleDateFormat(MONTH_DATE_PATTERN);
    /**
     * 日期Format
     */
    public static final SimpleDateFormat WEEK_DATE_FORMAT = new SimpleDateFormat(WEEK_DATE_PATTERN);

    /**
     * 周榜日期Format
     */
    public static final SimpleDateFormat WEEK_RANGE_DATE_FORMAT = new SimpleDateFormat(WEEK_RANGE_DATE_PATTERN);


    public static final DateTimeFormatter LOCAL_MONTH_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern(RankConstant.MONTH_DATE_PATTERN)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .toFormatter();

    public static final DateTimeFormatter LOCAL_WEEK_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern(RankConstant.WEEK_DATE_PATTERN)
            .toFormatter();

    public static final DateTimeFormatter LOCAL_WEEK_RANGE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern(RankConstant.WEEK_RANGE_DATE_PATTERN)
            .toFormatter();

    /**
     * 新能源EnergyType类型
     */
    public static final List<String> NEW_ENERGY_TYPE_LIST = Arrays.asList("4", "5", "6");
    public static final List<Integer> NEW_ENERGY_TYPE_INT_LIST = Arrays.asList(4, 5, 6);
}
