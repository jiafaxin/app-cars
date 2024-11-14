package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.jiage.SeriesJiageComponent;
import com.autohome.app.cars.service.components.owner.SeriesGaizhuangComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 更新价格信息:一个小时更新一次
 * 0 10 0/1 * * ?
 */
@JobHander("SeriesGaizhuangJob")
@Service
public class SeriesGaizhuangJob extends IJobHandler {

    @Autowired
    SeriesGaizhuangComponent service;

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
