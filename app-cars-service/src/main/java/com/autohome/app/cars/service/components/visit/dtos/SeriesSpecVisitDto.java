package com.autohome.app.cars.service.components.visit.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/5/8
 */
@Data
public class SeriesSpecVisitDto {

    private String dt;
    private String seriesName;
    private int allUv;
    private List<SpecVisit> visitList;

    @Data
    public static class SpecVisit {
        private int specId;
        private String specName;
        private String price;
        private int uv;
    }
}
