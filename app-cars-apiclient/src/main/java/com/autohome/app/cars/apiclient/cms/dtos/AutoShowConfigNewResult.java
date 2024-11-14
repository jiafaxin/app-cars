package com.autohome.app.cars.apiclient.cms.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class AutoShowConfigNewResult {

    private int totalcount;

    private List<AutoShowItem> items = new ArrayList<>();

    @Data
    public static class AutoShowItem {
        private Integer carStatus;
        private Integer seriesId;
        private Integer brandId;
        private Integer carType;
        private String carTypeName;
        private Integer carAction;
        private String carActionName;
        private Integer newenergyTag;
        private String articleUrl;
        private String memo;
        private Integer orderNum;
        private String tagIds;
    }
}
