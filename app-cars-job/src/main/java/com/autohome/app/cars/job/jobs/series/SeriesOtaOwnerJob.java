package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.car.SeriesOtaOwnerComponent;
import com.autohome.app.cars.service.components.vr.SeriesVrComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * otaowner数据更新 一小时一次
 * 0 20 * * * ?
 */
@JobHander("SeriesOtaOwnerJob")
@Service
public class SeriesOtaOwnerJob extends IJobHandler {

    @Autowired
    SeriesOtaOwnerComponent service;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        int totalMinutes = strings == null || strings.length == 0 || StringUtils.isBlank(strings[0]) ? 10 : Integer.parseInt(strings[0]);
        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
