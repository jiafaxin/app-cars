package com.autohome.app.cars.service.components.koubei.dtos;

import lombok.Data;

@Data
public class SeriesKouBeiDto {
    int seriesId;

    Bean bean;
    ScoreInfo scoreInfo;

    @Data
    public static class Bean{
        String subTitle;
        String scoreTitle;
        String appScheme;
    }

    @Data
    public static class ScoreInfo{
        int evalCount;
        double average;
    }

}
