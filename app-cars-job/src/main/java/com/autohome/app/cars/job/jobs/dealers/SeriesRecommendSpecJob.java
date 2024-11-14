package com.autohome.app.cars.job.jobs.dealers;

import com.autohome.app.cars.service.components.dealer.SeriesRecommendSpecComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@JobHander("SeriesRecommendSpecJob")
@Service
public class SeriesRecommendSpecJob extends IJobHandler {
    @Resource
    private SeriesRecommendSpecComponent component;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 240 : Integer.parseInt(params[0]);
        String fileName = XxlJobFileAppender.contextHolder.get();
        component.refreshAll(totalMinutes, log -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(log);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
