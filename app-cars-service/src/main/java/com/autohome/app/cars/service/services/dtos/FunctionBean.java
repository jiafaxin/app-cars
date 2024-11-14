package com.autohome.app.cars.service.services.dtos;

import lombok.Data;

@Data
public class FunctionBean {
    private String iconurl;
    private String linkurl;
    private String title;
    private String subtitle;
    private String subtitlehighlight;
    private int typeid;
    private String code;
    private PvItem pvitem;
    private int scale = 1;

    @Data
    public static class LocationBeansTest{
        private int seriesid ;
        private String location2title;
        private String location2subtitle;
        private String location2url;
        private String location3title;
        private String location3subtitle;
        private String location3url;
    }
}
