package com.autohome.app.cars.apiclient.car.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : zzli
 * @description : TODO
 * @date : 2024/6/7 20:34
 */
@NoArgsConstructor
@Data
public class MonthRankDto {

    private Integer id;
    private String seriesid;
    private Integer min_guidance_price;
    private Integer max_guidance_price;
    private String month;
    private Integer salecnt;
    private String manu_type;
    private String level;
    private Integer is_newenergy;

    private Integer rnnum;
    private Integer rn;
    private Integer scorecnt;
    private Integer pre_rn;
    private Integer brandid;
    private Integer pre_salecnt;
}
