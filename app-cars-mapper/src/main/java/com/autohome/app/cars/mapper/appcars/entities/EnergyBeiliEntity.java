package com.autohome.app.cars.mapper.appcars.entities;

import com.google.type.Decimal;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyBeiliEntity {
    Long spec_id;
    int season;
    BigDecimal drive_range;
    BigDecimal energy_cost;
    String zone_id;


    //以下字段不是从db取的
    int seriesId;
    int specState;
    int energyType;
    int brandId;
    int levelId;
    int official_range;
    String spec_name;
}
