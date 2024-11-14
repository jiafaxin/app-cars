package com.autohome.app.cars.service.components.uv.dto;

import lombok.Data;

@Data
public class EsSeriesUvItemDto {

    /**
     * seriesid : 12
     * hour : 12
     * date : 2022
     */

    private int seriesId;
    private int hour;
    private String date;
    private long count;
    private String dateHour;
    private int levelId;
    private int state;
}
