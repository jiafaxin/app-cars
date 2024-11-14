package com.autohome.app.cars.service.components.car.dtos.paramconfig;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SpecParamConfigDto {

    private int specId;
    private Map<String,Item> itemMap = new HashMap(); //key：二级id_三级id

    @Data
    public static class Item{

        private String name;
        private String value;
        private int cornertype;
        private String subvalue;
        private String cornerscheme;
        private String priceinfo;
        private String logo;
        private List<SubItem> sublist =new ArrayList<>();
    }
    @Data
    public static class SubItem{
        private String name;
        private String value;
        private String priceinfo;
        private int subitemid;
        private String logo;
    }

}
