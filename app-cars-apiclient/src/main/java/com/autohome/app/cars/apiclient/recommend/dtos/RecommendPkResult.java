package com.autohome.app.cars.apiclient.recommend.dtos;


import lombok.Data;

import java.util.List;

@Data
public class RecommendPkResult {
    private static final long serialVersionUID = 1L;
    private int returncode = 0;
    private String message = "";
    private PkResultInfo result;

    @Data
    public static class PkResultInfo {

        private List<SeriesSpecPair> data;

        private String deviceId;

        private String pvid;

        private String rid;

        private String pvdata;
    }
}