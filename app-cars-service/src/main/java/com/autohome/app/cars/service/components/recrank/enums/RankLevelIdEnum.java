package com.autohome.app.cars.service.components.recrank.enums;

import lombok.Getter;

@Getter
public enum RankLevelIdEnum {
    ALL_CAR_EMPTY("", "202", "车型%s总榜"),
    ALL_CAR("0", "202", "车型%s总榜"),
    /**
     * 轿车
     */
    ALL_SEDAN("1,2,3,4,5,6", "201", "轿车%s榜"),
    MINI_SEDAN("1", "1", "微型轿车%s榜"),
    SMALL_SEDAN("2", "2", "小型轿车%s榜"),
    COMPACT_SEDAN("3", "3", "紧凑型轿车%s榜"),
    MID_SEDAN("4", "4", "中型轿车%s榜"),
    MID_LARGE_SEDAN("5", "5", "中大型轿车%s榜"),
    LARGE_SEDAN("6", "6", "大型轿车%s榜"),
    SPORTS_CAR("7", "7", "跑车%s榜"),
    WEIMIAN("11","11","微面%s榜"),
    WEIKA("12","12","微卡%s榜"),
    QINGKE("13","13","轻客%s榜"),
    WIXINGPIKA("14","14","微型皮卡%s榜"),
    GAODUANPIKA("15","15","高端皮卡%s榜"),
    PIKA("14,15","301","皮卡%s榜"),
    /**
     * SUV
     */
    ALL_SUV("16,17,18,19,20", "200", "SUV%s榜"),

    SMALL_SUV("16", "16", "小型SUV%s榜"),
    COMPACT_SUV("17", "17", "紧凑型SUV%s榜"),
    MID_SUV("18", "18", "中型SUV%s榜"),
    MID_LARGE_SUV("19", "19", "中大型SUV%s榜"),
    LARGE_SUV("20", "20", "大型SUV%s榜"),
    // MPV
    MPV("21,22,23,24", "8", "MPV%s榜"),
    // 全部新能源
    ALL_NEW_ENERGY("201908", "100", "新能源%s榜"),
    // 插电式混合动力
    PLUG_IN_HYBRID("202105","101", "新能源%s榜"),
    // 纯电动
    PURE_ELECTRIC("202104", "102", "新能源%s榜"),
    // 增程式
    EXTENDED_RANGE("202106","103", "新能源%s榜");



    private final String srcId;
    private final String targetId;
    private final String rankNameScheme;

    RankLevelIdEnum(String srcId, String targetId, String rankName) {
        this.srcId = srcId;
        this.targetId = targetId;
        this.rankNameScheme = rankName;
    }

    public static RankLevelIdEnum getInstance(String srcId) {
        RankLevelIdEnum[] values = values();
        for (RankLevelIdEnum value : values) {
            if (value.srcId.equals(srcId)) {
                return value;
            }
        }
        return null;
    }
}