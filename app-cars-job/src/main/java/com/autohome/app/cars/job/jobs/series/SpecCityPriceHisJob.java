package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.hangqing.SpecCityPriceHisComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander("SpecCityPriceHisJob")
@Service
public class SpecCityPriceHisJob extends IJobHandler {

    @Autowired
    SpecCityPriceHisComponent service;

    @Override
    public ReturnT<String> execute(String... params) {
        String fileName = XxlJobFileAppender.contextHolder.get();

        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0])
                ? 360
                : Integer.parseInt(params[0]);

        service.refreshAllCity(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
