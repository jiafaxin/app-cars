package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.car.SeriesAttentionComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 原接口定制凌晨5点更新数据，我们延后10分钟
 * cron = "0 10 * * * ?"
 */
@JobHander("SeriesAttentionJob")
@Service
public class SeriesAttentionJob extends IJobHandler {

    @Autowired
    SeriesAttentionComponent service;

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
