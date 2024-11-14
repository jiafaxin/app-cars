package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.car.CarPriceChangeComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zzli
 * @description : 车型降价
 * @date : 2024/10/24 14:27
 */
@JobHander("CarPriceChangeJob")
@Service
public class CarPriceChangeJob extends IJobHandler {
    @Autowired
    CarPriceChangeComponent service;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        service.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
