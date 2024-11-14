package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.misc.SeriesCityHotNewsComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chengjincheng
 * @date 2024/7/19
 */
@JobHander("SeriesCityHotNewsJob")
@Service
public class SeriesCityHotNewsJob extends IJobHandler {

    @Autowired
    SeriesCityHotNewsComponent service;

    @Override
    public ReturnT<String> execute(String... params) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0])
                ? 3
                : Integer.parseInt(params[0]);
        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
