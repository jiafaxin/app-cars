package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecPicColorStatisticsEntity implements Serializable {
    int seriesId;
    int specId;
    int syearId;
    int syear;
    int specState;
    int colorId;
    int picClass;
    int picNumber;
    int clubPicNumber;
    float classOrder;
    int carState;
    int specYear;
    int price;
    String remarks;
}
