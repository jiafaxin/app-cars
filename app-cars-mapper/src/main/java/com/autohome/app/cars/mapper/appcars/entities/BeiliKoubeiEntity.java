package com.autohome.app.cars.mapper.appcars.entities;

import lombok.Data;

/**
 * @author : zzli
 * @description : 北理、口碑新能源数据
 * @date : 2024/2/27 13:53
 */
@Data
public class BeiliKoubeiEntity {
    int series_id;
    int spec_id;
    int season;
    double drive_range;
    double energy_cost;
    int zone_id;

    @Data
    public static class CityZone{
        int city_id;
        int kb_zone_id;
    }
}
