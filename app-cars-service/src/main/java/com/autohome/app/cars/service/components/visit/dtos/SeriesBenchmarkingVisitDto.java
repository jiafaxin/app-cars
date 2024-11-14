package com.autohome.app.cars.service.components.visit.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/5/8
 */
@Data
public class SeriesBenchmarkingVisitDto {

    private String dt;
    private String seriesName;
    private List<BenchmarkingVisit> visitList;

    @Data
    public static class BenchmarkingVisit {
        private int seriesId;
        private String seriesName;
        private int pv;
        private int uv;
        private int rankNum;
    }
}
