package com.autohome.app.cars.common.enums;

import lombok.Getter;

/**
 * 车系动态类型枚举
 *
 * @author zhangchengtao
 * @date 2024/8/13 10:49
 */
@Getter
public enum SeriesSubscribeNewsEnum {
    IMAGE(1, 30601, "图片", 101,1),
    CONFIG(2, 30605, "参配", 102,2),
    MARKET_PRICE(3, 30602, "上市价格", 103,3),
    CAR_WORK(4, 30604, "提车作业", 801,4),
    RANK_MONTH(5, 30603, "月榜销量更新", 107,5),
    RANK_WEEK(6, 30603, "周榜销量更新", 107,5),
    SERIES_GUIDE_PRICE(7, 30606, "降价-车系指导价", 100,6),
    SPEC_GUIDE_PRICE(8, 30606, "降价-车型指导价", 100,6),
    SERIES_DEALER_PRICE(9, 30606, "降价-车系经销商价", 100,6),
    SPEC_DEALER_PRICE(10, 30606, "降价-车型经销商价", 100,6),
    CMS_NEWS(11, 30607, "图文资讯动态", 112,7),
    VIDEO_NEWS(12, 30607, "视频资讯动态", 112,7),

    ASSESS_REPORT(10001,30608,"估值报告", 10,10) //估值报告这个枚举我 是业务调用二手 自行获取处理的
    ;

    private final int type;
    /**
     * 端上组件化的卡片类型
     */
    private final int cardtype;
    /**
     * 订阅的biztype
     */
    private final int biztype;
    private final String desc;
    /**
     * pv上报类型
     */

    private final int pvtype;

    SeriesSubscribeNewsEnum(int type, int cardtype, String desc, int biztype, int pvtype) {
        this.type = type;
        this.cardtype = cardtype;
        this.desc = desc;
        this.biztype = biztype;
        this.pvtype = pvtype;
    }

    public static SeriesSubscribeNewsEnum getByType(int type) {
        for (SeriesSubscribeNewsEnum value : values()) {
            if (value.getType() == type) {
                return value;
            }
        }
        return null;
    }
}
