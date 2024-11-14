package com.autohome.app.cars.apiclient.che168.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class GetSeriesHotSpecsResult {

    private List<ListDTO> list = new ArrayList<>();

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        private Integer specid;
        private String specname;
        private String price;
        private String follow;
        private String dynamicprice;
        private String moreurl;

        private List<HotSpecDealerItem> list = new ArrayList<>();
    }

    @NoArgsConstructor
    @Data
    public static class HotSpecDealerItem {
        private String carname;
        private String price;
        private Integer specid;
        private String mileage;
        private String regdate;
        private String cityname;
        private String imageurl;
        private String url;
    }
}