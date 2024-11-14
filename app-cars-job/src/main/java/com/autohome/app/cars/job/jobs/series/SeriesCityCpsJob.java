package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.SeriesCityCpsComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@JobHander("SeriesCityCpsJob")
@Service
public class SeriesCityCpsJob extends IJobHandler {

    @Autowired
    SeriesCityCpsComponent service;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();

        int totalMinutes = strings == null || strings.length == 0 || StringUtils.isBlank(strings[0]) ? 60 : Integer.parseInt(strings[0]);

        service.refreshAll(totalMinutes,x->{
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
