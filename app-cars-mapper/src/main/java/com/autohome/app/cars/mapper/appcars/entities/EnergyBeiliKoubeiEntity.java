package com.autohome.app.cars.mapper.appcars.entities;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EnergyBeiliKoubeiEntity {
    int id;
    int series_id;
    int brand_id;
    int spec_id;
    int level_id;
    int state;
    //季节
    int season;
    //续航
    BigDecimal drive_range;
    //耗电
    BigDecimal energy_cost;
    //北理温区
    String bl_zone_id;
    //口碑温区
    int kb_zone_id;
    int energyType;
    String dataVersion;
    int dataFrom;
    int official_range;
    int is_max_range;
    int min_price;
    int max_price;
    String spec_name;
}
