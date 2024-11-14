package com.autohome.app.cars.apiclient.bfai.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SSeriesSortListResult {
    private Integer returncode;
    private String message;
    private ResultDTO result;

    @Data
    public static class ResultDTO {
        private String pvid;
        private String rid;
        private List<Integer> data;
        private String ext;
        private List<CarColorItem> data1;
    }

    @Data
    public static class CarColorItem {
        private int series_id;
        private String img_url;
        private String ext;
    }
}
