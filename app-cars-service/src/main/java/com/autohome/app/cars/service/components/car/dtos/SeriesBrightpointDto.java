package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 车系亮点相关
 */
@Data
public class SeriesBrightpointDto {
    int seriesId;
    /**
     * 魔方的h5亮点
     */
    Brightpoint brightpoint;
    /**
     * 参配的车系亮点
     */
    CarSeriesHighlight carSeriesHighlight;

    @Data
    public static class Brightpoint {
        String title;
        String url;
    }

    @Data
    public static class CarSeriesHighlight {
        int seriesId;
        int specId;
        /**
         * 有多少项亮点
         */
        int highlightCount;
        /**
         * 在售车型，指导价最高的两车型id
         */
        List<Integer> minPriceSpecIds = new ArrayList<>();
    }
}
