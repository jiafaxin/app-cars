package com.autohome.app.cars.job.controller;

import com.autohome.app.cars.service.components.visit.SeriesBenchmarkingVisitComponent;
import com.autohome.app.cars.service.components.visit.SeriesSpecVisitComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chengjincheng
 * @date 2024/5/9
 */
@RestController
@Slf4j
public class SeriesVisitController {

    @Autowired
    private SeriesBenchmarkingVisitComponent seriesBenchmarkingVisitComponent;

    @Autowired
    private SeriesSpecVisitComponent seriesSpecVisitComponent;

    @GetMapping(value = "/series/benchmarkingvisit/update", produces = "application/json;charset=utf-8")
    public String updateSeriesBenchmarkingVisit() {
        seriesBenchmarkingVisitComponent.refreshAll(log::info);
        return "success";
    }

    @GetMapping(value = "/series/specvisit/update", produces = "application/json;charset=utf-8")
    public String updateSeriesSpecVisit() {
        seriesSpecVisitComponent.refreshAll(log::info);
        return "success";
    }
}
