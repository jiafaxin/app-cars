package com.autohome.app.cars.job.jobs.common;

import com.autohome.app.cars.service.components.owner.BeiliKoubeiComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander("BeiliKoubeiComponentJob")
@Service
@Slf4j
public class BeiliKoubeiComponentJob  extends IJobHandler {

    @Autowired
    BeiliKoubeiComponent beiliKoubeiComponent;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        beiliKoubeiComponent.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
