package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeriesSpecDto {
    int seriesId;
    List<Item> items = new ArrayList<>();

    @Data
    public static class Item{
        int id;
        int state;
        int fuelType;
        int minPrice;
    }
}
