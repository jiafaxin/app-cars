package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.che168.SpecUsedCarPriceComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yangchengwei
 * @date 2024/3/19
 */
@JobHander("SpecUsedCarPriceJob")
@Service
public class SpecUsedCarPriceJob extends IJobHandler {

    @Autowired
    SpecUsedCarPriceComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();

        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 30 : Integer.parseInt(params[0]);

        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
