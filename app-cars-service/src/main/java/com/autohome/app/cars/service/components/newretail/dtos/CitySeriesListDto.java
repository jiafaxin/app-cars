package com.autohome.app.cars.service.components.newretail.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CitySeriesListDto {

    int cityId;
    List<Series> items = new ArrayList<>();

    @Data
    public static class Series{
        int seriesId;
    }
}
