package com.autohome.app.cars.service.components.recrank.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DriveRangeCostListDto {


    private int pageIndex;

    private int pageSize;

    private int count;

    private int pages;

    private List<ResultDto> seriesList = new ArrayList<>();

    @Data
    public static class ResultDto {


        @JsonProperty("seriesId")
        private Integer seriesId;
        @JsonProperty("pnglogo")
        private String pnglogo;
        @JsonProperty("brandId")
        private Integer brandId;
        @JsonProperty("specId")
        private Integer specId;
        @JsonProperty("levelId")
        private Integer levelId;
        @JsonProperty("minPrice")
        private Integer minPrice;
        @JsonProperty("maxPrice")
        private Integer maxPrice;
        @JsonProperty("state")
        private Integer state;
        @JsonProperty("energyType")
        private String energyType;
        @JsonProperty("seriesName")
        private String seriesName;
        @JsonProperty("specName")
        private String specName;
        @JsonProperty("season")
        private Integer season;
        @JsonProperty("driveRange")
        private Double driveRange;
        @JsonProperty("energyCost")
        private Double energyCost;
        @JsonProperty("rank")
        private Integer rank;
    }
}
