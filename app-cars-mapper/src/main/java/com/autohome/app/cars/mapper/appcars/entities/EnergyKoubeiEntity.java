package com.autohome.app.cars.mapper.appcars.entities;

import com.google.type.Decimal;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyKoubeiEntity {
    Long series_id;
    int season;
    BigDecimal drive_range;
    BigDecimal energy_cost;
    BigDecimal official_range;
    int zone_id;


    //以下字段不是从db取的，是另外赋值的
    int specId;
    int specState;
    int energyType;
    int brandId;
    int levelId;
    String spec_name;

}
