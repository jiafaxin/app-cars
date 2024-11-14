package com.autohome.app.cars.service.components.car.dtos.paramconfig;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class SpecColorListItemsDto {

    private int total;
    @Data
    public static class SpecItem{
        private int specid;
        private List<ColorItem> coloritems;
    }

    private List<SpecItem> specitems;

    @Data
    public static class ColorItem{
        private int id;
        private String name;
        private String value;
        private int picnum;
        private int clubpicnum;
        int price;
        String remark;
    }
}
