package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.mapper.popauto.entities.SpecConfigBagEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SpecConfigBagDto implements Serializable {

    private int specid;
    private List<ConfigBagValue> configbags = new ArrayList();

    @Data
    public static class ConfigBagValue{
        int id;
        int specid;
        int bagid;
        String name;
        String description;
        int price;
        String pricedesc;
        String imgurl;
    }

}
