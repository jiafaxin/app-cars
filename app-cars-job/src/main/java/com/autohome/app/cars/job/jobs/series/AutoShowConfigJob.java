package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.cms.AutoShowConfigComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 车展配置 30分钟执行一次
 * 0 0/30 * * * ?
 */
@JobHander("AutoShowConfigJob")
@Service
public class AutoShowConfigJob extends IJobHandler {

    @Autowired
    AutoShowConfigComponent autoShowConfigComponent;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        autoShowConfigComponent.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
