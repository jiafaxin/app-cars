package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BrandSeriesDto {
    private int brandId;

    List<FactoryItem> fctoryList = new ArrayList<>();

    @Data
    public static class FactoryItem{
        private int id;
        private String py;
        private List<SeriesItem> seriesList = new ArrayList<>();
    }

    @Data
    public static class SeriesItem{
        private int id;
        private int state;
    }
}
