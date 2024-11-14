package com.autohome.app.cars.service.components.car.dtos.paramconfig;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpecOutInnerColorDto {

    private int specid;
    private List<ColorItem> coloritems = new ArrayList<>();

    @Data
    public static class ColorItem{
        private int id;
        private String name;
        private String value;
        private int price;
        private String remarks;
        String picurl;
        private int piccount;
    }
}
