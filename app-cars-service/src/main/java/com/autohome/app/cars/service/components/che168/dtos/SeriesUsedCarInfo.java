package com.autohome.app.cars.service.components.che168.dtos;

import lombok.Data;

@Data
public class SeriesUsedCarInfo {

    int seriesId;
    String title;
    String subTitle = "";
    String jumpurl;

    /**
     * 车系顶部二手车询价链接
     */
    String jumpurl_base;

    /**
     * 二手车保值率
     */
    double rate;

    String pvareaid;
}
