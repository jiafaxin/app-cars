package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.im.SeriesCityImComponent;
import com.autohome.app.cars.service.components.newretail.NewRetailCitySeriesComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * IM
 * 0 5 0/2 * * ?
 */
@JobHander("NewRetailCityJob")
@Service
public class NewRetailCityJob extends IJobHandler {

    @Autowired
    NewRetailCitySeriesComponent service;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        service.refreshAll(x->{
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
