package com.autohome.app.cars.mapper.appcars.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by dx on 2024/7/9
 */
@Data
@NoArgsConstructor
public class SaleMonthEnergyTypeRankSourceEntity {
    private long id;
    private int seriesid;
    private int brandid;
    private String month;
    private Long salecnt;
    private Integer energy_type;//能源类型 1-燃油、4-纯电动、5-插电、6-增程
    private Integer is_newenergy;
    private Date created_stime;
    private Date modified_stime;
}
