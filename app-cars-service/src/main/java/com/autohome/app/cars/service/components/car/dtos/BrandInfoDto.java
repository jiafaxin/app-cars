package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

@Data
public class BrandInfoDto extends BrandDetailDto {

    //首字母
    private String letter;

    private int sort;

    private int state;

    //是否新能源
    private int isNewEnergy;
}
