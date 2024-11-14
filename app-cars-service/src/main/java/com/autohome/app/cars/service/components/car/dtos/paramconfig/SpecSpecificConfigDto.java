package com.autohome.app.cars.service.components.car.dtos.paramconfig;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpecSpecificConfigDto implements Serializable {

    private int specid;
    private List<ConfigItem> configitems;

    @Data
    public static class ConfigItem implements Serializable{

        private int baikeid;

        private String baikeurl;

        private int configid;

        private String name;

        private int specid;

        private String value;

        private String price;

    }

}
