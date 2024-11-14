package com.autohome.app.cars.mapper.car.entities;

import lombok.Data;

import java.util.Date;

@Data
public class HqVideoBaseEntity {
    int id;
    String logo;
    int point_id;
    String mid;

    Date modified_stime;
}
