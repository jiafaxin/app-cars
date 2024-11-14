package com.autohome.app.cars.common.enums;

import lombok.Getter;

@Getter
public enum RankRedisKeyEnum {
    // 数据格式: [2023-10-11 16:46:33]
    MONTH_LAST_PUSH_NEWS_DATE("sale_month_rank_last_push_news_time", "月榜最后一次推送资讯时间"),

    // 数据格式: [2023-10-11 16:46:33]
    WEEK_LAST_PUSH_NEWS_DATE("sale_week_rank_last_push_news_time", "周榜最后一次推送资讯时间"),

    // 数据格式: [0/1/2] 0: 无榜单更新  1: 月榜更新 2: 周榜更新
    LAST_PUSH_INFORMATION_TYPE("last_push_information_type", "上次推送资讯榜单"),

    // 数据格式: [2023-10: 2]
    MONTH_SAMETIME_PUSH_TIMES("month_sametime_push_times", "月榜相同日期数据推送次数"),

    // 数据格式: [2023-10-11: 2]
    WEEK_SAMETIME_PUSH_TIMES("week_sametime_push_times", "周榜相同日期数据推送次数"),

    // 数据格式: [2023-09]
    MONTH_LATEST_DATE("sale_month_rank_last_month", "月榜最新一条数据的month"),

    // 数据格式: [2023-10-11 16:46:33]
    MONTH_UPDATE_TIMESTAMP("salerank_badge", "月榜最新一条数据的created_stime"),

    /**
     * 数据格式:
     * {
     * "weekDay": "2023-10-17",
     * "updateTime": "2023-10-24 22:24:37"
     * }
     * weekDay: 周榜最新一条数据的week_day
     * updateTime: 最新一条数据的 modified_stime
     */
    WEEK_UPDATE_TIMESTAMP("sale_week_rank_badge", "最新一条周榜数据的信息[JSON]"),

    // 数据格式: [2023-10]
    MONTH_LAST_PASSIVE_UPDATE_TIME("sale_month_rank_last_passive_update_time", "月榜: 数仓最后一次主动调用推送时间"),

    // 数据格式: [2023-10-17]
    WEEK_LAST_PASSIVE_UPDATE_TIME("sale_week_rank_last_passive_update_time", "周榜: 数仓最后一次主动调用推送时间"),

    SALE_RANK_MONTH_TOP_10("sale_rank_month_top_10", "月榜TOP10车系ID"),
    SALE_RANK_WEEK_TOP_10("sale_rank_week_top_10", "周榜TOP10车系ID"),
    NEW_ENERGY_SALE_RANK_MONTH_TOP_10("new_energy_sale_rank_month_top_10", "新能源月榜TOP10车系ID"),
    NEW_ENERGY_SALE_RANK_WEEK_TOP_10("new_energy_sale_rank_week_top_10", "新能源周榜TOP10车系ID");
    private final String key;
    private final String desc;

    RankRedisKeyEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}
