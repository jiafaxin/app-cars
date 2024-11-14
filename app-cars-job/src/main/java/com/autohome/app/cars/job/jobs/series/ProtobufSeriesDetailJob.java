package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.car.ProtobufSeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander("ProtobufSeriesDetailJob")
@Service
public class ProtobufSeriesDetailJob extends IJobHandler {

    @Autowired
    ProtobufSeriesDetailComponent protobufSeriesDetailComponent;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();

        protobufSeriesDetailComponent.refreshAll(x->{
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
