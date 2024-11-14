package com.autohome.app.cars.apiclient.rank.dtos;

import lombok.Data;

import java.util.List;

@Data
public class MotoRankResult {

    private int returncode;
    private String message;
    private ResultBean result;

    @Data
    public static class ResultBean {

        private int seriesCount;
        private List<ListBean> list;

        @Data
        public static class ListBean {

            private int seriesId;
            private String seriesName;
            private int minPrice;
            private int maxPrice;
            private String seriesLogo;
            private int uv;
            private int pv;
            private int levelId;
            private String levelName;

        }
    }
}
