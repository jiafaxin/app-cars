package com.autohome.app.cars.mapper.appcars.entities;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author zhangchengtao
 * @date 2024/8/13 11:02
 */
@Data
public class SeriesSubscribeNewsEntity {
    private int series_id;
    private int spec_id;
    private int city_id;
    /**
     * 动态类型: 1:图片 2:参配 3:上市价格 4:提车作业 5:销量更新 6:降价-车系指导价 7:降价-车型指导价 8:降价-车系经销商价 9:降价-车型经销商价 10:资讯动态
     */
    private int biz_type;
    private int is_show;
    private Timestamp display_time;
    private String data;
}
