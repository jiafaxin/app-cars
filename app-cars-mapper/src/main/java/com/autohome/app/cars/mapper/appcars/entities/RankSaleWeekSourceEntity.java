package com.autohome.app.cars.mapper.appcars.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author zhangchengtao
 * @date 2024/5/6 11:16
 */
@Data
@NoArgsConstructor
public class RankSaleWeekSourceEntity {
    private long id;
    private int brandid;
    private String seriesid;
    private String energy_sale_count;
    private Long min_guidance_price;
    private Long max_guidance_price;
    private String week_day;
    private String weekRange;
    private Long salecnt;
    private String manu_type;
    private String level;
    private Integer is_newenergy;
    private Timestamp created_stime;
    private Timestamp modified_stime;
    private Long procnt;
    private int rnnum;

    /**
     * 销量相同的，此字段排名也相同
     */
    private int rn;

    private long scorecnt;
    /**
     * 上月销量排名
     */
    private int pre_rn;
}
