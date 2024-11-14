package com.autohome.app.cars.service.components.recrank.moto.dto;

import lombok.Data;

@Data
public class MotoRankDto {
    private int seriesId;
    private String seriesName;
    private int minPrice;
    private int maxPrice;
    private String seriesLogo;
    private int uv;
    private int pv;
    private int levelId;
    private String levelName;

}
