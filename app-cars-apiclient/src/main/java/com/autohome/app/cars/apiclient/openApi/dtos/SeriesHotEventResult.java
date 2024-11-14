package com.autohome.app.cars.apiclient.openApi.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class SeriesHotEventResult {

    private Integer returncode;
    private String message;
    private String bsdata;
    private Integer number;
    private Object debuginfo;
    private ResultDTO result = new ResultDTO();
    private Boolean not_enough;
    private String requestid;
    private String loguuid;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        private List<ItemlistDTO> itemlist = new ArrayList<>();

        @NoArgsConstructor
        @Data
        public static class ItemlistDTO {
            private String brandIds;
            private String eventId;
            private String eventName;
            private Integer hotRank;
            private String hotTag;
            private Integer hotType;
            private Integer hotValue;
            private String itemList;
            private String seriesIds;
            private String appJump;
            private String firstRankTime;
            private String keyword;
        }
    }
}
