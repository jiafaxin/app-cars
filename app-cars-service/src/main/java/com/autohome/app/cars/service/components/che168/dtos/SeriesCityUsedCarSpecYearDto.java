package com.autohome.app.cars.service.components.che168.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/5/14
 */
@Data
public class SeriesCityUsedCarSpecYearDto {

    private List<SUsedCarSpecList_Spec> list;

    @Data
    public static class SUsedCarSpecList_Spec {
        private int specid;
        private String specname;
        private String price;
        private String follow;
        private String dynamicprice;
        private String moreurl;
        private List<SUsedCarSpecList_Dealer> list;
    }

    @Data
    public static class SUsedCarSpecList_Dealer {
        private int infoid;
        private String carname;
        private String price;
        private int brandid;
        private int seriesid;
        private int specid;
        private String mileage;
        private String regdate;
        private String cityname;
        private String cartype;
        private int carlevel;
        private int fromtype;
        private String imageurl;
        private String[] tags;
        private String url;
    }
}