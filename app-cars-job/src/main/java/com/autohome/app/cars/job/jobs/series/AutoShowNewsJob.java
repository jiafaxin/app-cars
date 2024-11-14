package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.cms.AutoShowConfigComponent;
import com.autohome.app.cars.service.components.cms.AutoShowNewsComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 车展资讯 10分钟执行一次
 * 0 0/10 * * * ?
 */
@JobHander("AutoShowNewsJob")
@Service
public class AutoShowNewsJob extends IJobHandler {

    @Autowired
    AutoShowNewsComponent autoShowNewsComponent;

    @Override
    public ReturnT<String> execute(String... strings) {
        int totalMinutes = strings == null || strings.length == 0 || StringUtils.isBlank(strings[0]) ? 10 : Integer.parseInt(strings[0]);
        String fileName = XxlJobFileAppender.contextHolder.get();
        autoShowNewsComponent.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
