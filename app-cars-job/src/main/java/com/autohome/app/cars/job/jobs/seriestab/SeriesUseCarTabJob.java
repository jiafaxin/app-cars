package com.autohome.app.cars.job.jobs.seriestab;

import com.autohome.app.cars.service.components.owner.SeriesPlayCarComponent;
import com.autohome.app.cars.service.components.owner.SeriesUseCarComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 玩车tab和内容
 * 定时执行：每1小时
 * 0 0 0/1 * * ?
 */
@JobHander("SeriesUseCarTabJob")
@Service
public class SeriesUseCarTabJob extends IJobHandler {

    @Autowired
    SeriesUseCarComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 30 : Integer.parseInt(params[0]);

        service.refreshOne(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        }, 692, 110100);

        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
