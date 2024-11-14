package com.autohome.app.cars.service.components.club.dtos;

import lombok.Data;

@Data
public class SeriesClub {
    private int seriesId;
    private String subTitle = "";
    private String jumpUrl = "";
    private String bbsName = "";

    private String qaSubTitle = "";
    private String qaJumpUrl = "";
}
