package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.car.SeriesTestDataComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zzli
 * @description : 车系实测、超测
 * 0 0/10 * * * ?  10分钟执行一次
 * @date : 2024/1/29 19:29
 */
@JobHander("SeriesTestDataJob")
@Service
public class SeriesTestDataJob extends IJobHandler {
    @Autowired
    SeriesTestDataComponent seriesTestDataComponent;

    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        seriesTestDataComponent.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
