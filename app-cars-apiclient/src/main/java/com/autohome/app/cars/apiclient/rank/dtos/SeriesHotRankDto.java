package com.autohome.app.cars.apiclient.rank.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/6/12
 */
@Data
public class SeriesHotRankDto {


    private int returncode;
    private String message;
    private Result result;


    @Data
    public static class Result {

        private List<SeriesData> seriesData;
        private Extras extras;

    }

    @Data
    public static class SeriesData {

        private int levelId;
        private String levelName;
        private List<Series> series;

    }

    @Data
    public static class Series {

        private int rank;
        private int seriesId;
        private String seriesName;
        private int levelId;
        private int state;
        private long attNum;
        private long mixPrice;
        private long maxPrice;

    }

    @Data
    public static class Extras {

        private int provinceId;

    }


}
