package com.autohome.app.cars.apiclient.owner.dtos;

import lombok.Data;

import java.util.List;
import java.util.TreeMap;

/**
 * @author : zzli
 * @description : 北理数据
 * @date : 2024/2/21 17:18
 */
@Data
public class BeiliStatisticsResult {
    private Result result;


    @Data
    public static class Result {

        private List<StatisSpecInfo> specInfoList;
        private TreeMap<String, BeiliCleanDataDto> seasonData;
        private TreeMap<String, Integer> temperatureDriveRangeRate;

        private TreeMap<Integer, Double> totalSlowChaMoneyList;
        private TreeMap<Integer, Double> totalFastChaMoneyList;
        private String winterLabScheme;
        private Integer currentSpecId;
        private List<NewSameCarDto> newSameCarList;
    }

    @Data
    public static class StatisSpecInfo {

        private Integer officialDriveRange;

        protected Integer factDriveRange;

        protected Double driveRate;

        protected Double factEnergy100;

        protected Double factChaSpeed;

        private Integer seriesId;

        private String seriesName;

        private Integer specId;

        private String specName;

        protected Integer minprice = 0;

        protected Integer maxprice = 0;

        protected String seriesPngLogo = "";

        protected String specLogo = "";
    }

    @Data
    public static class BeiliCleanDataDto {
        /**
         * fact_drive_range
         */
        protected Integer factDriveRange;
        protected Double driveRate;
        /**
         * fact_energy100
         */
        protected Double factEnergy100;

        protected Double volumeScore;

        protected Double factDriveRangeDouble;

        protected Double factChaSpeed;

        protected Double slowChaMoney;

        protected Double fastChaMoney;

        protected List<SameSeriesBeiliDataDto> sameCarList;

        private TreeMap<Integer, Double> reduceDriveRangeList;

        private TreeMap<Integer, Double> reduceVolumeScoreList;

        @Data
        public static class SameSeriesBeiliDataDto {
            protected String brandName;
            private Integer seriesId;
            private Integer specId;
            protected String seriesName;
            private Integer officialDriveRange;
            protected Integer factDriveRange;
            protected Double driveRate;
            protected Double factEnergy100;
            protected Double factChaSpeed;
        }
    }

    @Data
    public static class NewSameCarDto {
        private String brandName;
        private Integer seriesId;
        private String seriesName;
        private Integer specId;
        private Integer officialDriveRange;
        private List<SeasonData> sameCarSeasonList;

        @Data
        public static class SeasonData {
            private Integer factDriveRange;
            private Double driveRate;
            private Double factEnergy100;
            private Double factChaSpeed;
            private String season;
        }
    }
}
