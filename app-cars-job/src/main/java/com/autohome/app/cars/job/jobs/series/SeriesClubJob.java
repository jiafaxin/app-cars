package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.club.SeriesClubComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 更新论坛信息:一个小时更新一次
 * 0 15 0/1 * * ?
 */
@JobHander("SeriesClubJob")
@Service
public class SeriesClubJob extends IJobHandler {

    @Autowired
    SeriesClubComponent service;

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
