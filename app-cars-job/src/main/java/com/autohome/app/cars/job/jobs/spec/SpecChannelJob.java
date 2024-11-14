package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.dealer.SpecChannelComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chengjincheng
 * @date 2024/4/28
 */
@JobHander("SpecChannelJob")
@Service
public class SpecChannelJob extends IJobHandler {

    @Autowired
    SpecChannelComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();

        service.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
