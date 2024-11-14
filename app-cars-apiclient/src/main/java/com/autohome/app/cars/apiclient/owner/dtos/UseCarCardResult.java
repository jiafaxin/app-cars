package com.autohome.app.cars.apiclient.owner.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class UseCarCardResult {

    private Integer seriesid;
    private Integer specid;
    private List<CardsDTO> cards = new ArrayList<>();
    private List<TopConfigItemsDTO> topConfigItems = new ArrayList<>();
    private List<?> contentItems;
    private List<NewContentItemsDTO> newContentItems;
    private String moreCarsInfoUrl;

    @NoArgsConstructor
    @Data
    public static class CardsDTO {
        private String title;
        private String code;
        private String subtitle;
        private String data;
        private String imgUrl;
        private String appHref;
        private String mhref;
        private int typeid;
    }

    @NoArgsConstructor
    @Data
    public static class TopConfigItemsDTO {
        private String title;
        private String code;
        private String subtitle;
        private String imgUrl;
        private String imgUrlForRN;
        private String appHref;
        private String mhref;
        private int typeid;
    }

    @NoArgsConstructor
    @Data
    public static class NewContentItemsDTO {
        private String content;
        private List<String> coverImg;
        private String label;
        private String labelUrl;
        private String desc;
        private Integer type;
        private String jumpUrl;
        private String updateTime;
        private Integer sort;
    }
}
