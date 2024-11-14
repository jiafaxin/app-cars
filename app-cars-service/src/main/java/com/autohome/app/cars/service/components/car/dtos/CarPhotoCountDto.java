package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

@Data
public class CarPhotoCountDto {
    public CarPhotoCountDto(){

    }
    int specId;
    int seriesId;
    int count;
    int colorId;
    String colorValue;
    String colorName;
    int colorType;

}
