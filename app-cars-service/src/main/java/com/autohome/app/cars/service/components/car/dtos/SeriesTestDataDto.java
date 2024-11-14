package com.autohome.app.cars.service.components.car.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : 实测，超测试
 * @date : 2024/1/30 14:19
 */
@Data
public class SeriesTestDataDto {
    /**
     * 实测
     */
    List<SeriesTestData119Dto> TestData = new ArrayList<>();
    /**
     * 冬测（超测）
     */
    List<SeriesTestDataWinter120Dto> TestWinterData = new ArrayList<>();

    @Data
    public static class SeriesTestData119Dto {
        private int specId;
        private int fueltypedetail;
        private Integer standardId;
        private Integer dataId;
        private Integer isGenerate;
        private List<TestItemSummary> testItemlist = new ArrayList<>();
    }

    @Data
    public static class SeriesTestDataWinter120Dto {
        private Integer specId;
        private int fueltypedetail;
        private Integer standardId;
        private Integer dataId;
        private Integer isGenerate;
        private String zixunVideoId;
        private String videoId;
        private List<TestWinterItemSummary> testItemlist = new ArrayList<>();
    }

    @Data
    public static class TestItemSummary {
        private String name;
        private String showValue;
        private String unit;
        @JsonIgnore
        private Integer itemId;
        @JsonIgnore
        private Integer specId;
        @JsonIgnore
        private Integer dataId;
    }

    @Data
    public static class TestWinterItemSummary {
        private String name;
        private String showValue;
        private String newValue;
        private String unit;
        private String level3name;
        private String level2name;
        private String level1name;
    }
}
