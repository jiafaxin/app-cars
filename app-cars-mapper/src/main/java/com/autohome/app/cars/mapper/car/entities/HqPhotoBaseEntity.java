package com.autohome.app.cars.mapper.car.entities;

import lombok.Data;

import java.util.Date;

@Data
public class HqPhotoBaseEntity {
    int id;
    String url;
    int point_id;
    Date modified_stime;
}
