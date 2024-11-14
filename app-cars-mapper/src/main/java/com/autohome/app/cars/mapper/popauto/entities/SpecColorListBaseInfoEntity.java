package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecColorListBaseInfoEntity implements Serializable {
    int seriesId;
    int specId;
    int colorId;
    int picNumber;
    int clubPicNumber;
    int price;
    String remarks;
}
