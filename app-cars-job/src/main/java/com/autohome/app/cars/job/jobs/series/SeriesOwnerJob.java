package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.owner.SeriesOwnerComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import com.autohome.job.core.rpc.netcom.jetty.server.JettyServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 车系车主实拍数据更新
 * 0 30 * * * ?
 */
@JobHander("SeriesOwnerJob")
@Service
public class SeriesOwnerJob extends IJobHandler {

    @Autowired
    SeriesOwnerComponent service;

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
