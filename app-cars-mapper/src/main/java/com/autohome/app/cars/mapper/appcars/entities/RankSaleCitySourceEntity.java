package com.autohome.app.cars.mapper.appcars.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 城市榜源数据实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankSaleCitySourceEntity {
    private long id;
    private String seriesid;
    private String energy_sale_count;
    private String brandid;
    private Long min_guidance_price;
    private Long max_guidance_price;
    private String cityid;
    private String month;
    private Long salecnt;
    private String manu_type;
    private Integer energy_type;
    private String level;
    private Integer is_newenergy;
    private Timestamp created_stime;
    private Timestamp modified_stime;

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
