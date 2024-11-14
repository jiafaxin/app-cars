package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.car.SeriesBrightpointComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zzli
 * @description : 车系亮点，一天执行一次
 * @date : 2024/3/6 19:53
 */
@JobHander("SeriesBrightpointJob")
@Service
public class SeriesBrightpointJob extends IJobHandler {
    @Autowired
    SeriesBrightpointComponent seriesBrightpointComponent;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        seriesBrightpointComponent.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
