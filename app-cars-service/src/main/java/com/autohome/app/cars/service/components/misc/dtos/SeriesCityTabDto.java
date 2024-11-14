package com.autohome.app.cars.service.components.misc.dtos;

import lombok.Data;

@Data
public class SeriesCityTabDto {


    private int seriesId;

    /**
     * 是否有二手车数据
     */
    private int hasErShouData;

    /**
     * 是否有改装数据
     */
    private int hasGaizhuangWithRefitData;
}
