package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

@Data
public class ColorStatisticsDto {
    int id;
    String name;
    String value;
    int picCount;
    int clubpiccount;
    int isonsale;
}
