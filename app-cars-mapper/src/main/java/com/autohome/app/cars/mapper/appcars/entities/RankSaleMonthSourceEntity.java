package com.autohome.app.cars.mapper.appcars.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchengtao
 * @date 2024/4/22 13:50
 */
@Data
@NoArgsConstructor
public class RankSaleMonthSourceEntity {
    private int id;

    private String seriesid;

    private String energy_sale_count;

    private Integer energy_type;

    private long min_guidance_price;

    private long max_guidance_price;

    private String month;

    private Integer salecnt;

    private String manu_type;

    private String level;

    private Integer is_newenergy;

    private java.sql.Timestamp created_stime;

    private java.sql.Timestamp modified_stime;

    private int rnnum;

    /**
     * 销量相同的，此字段排名也相同
     */
    private int rn;

    private int scorecnt;
    /**
     * 上月销量排名
     */
    private int pre_rn;
    private int brandid;

    /**
     * 上月销量
     */
    private Integer pre_salecnt = 0;

    private boolean is_del;
}
