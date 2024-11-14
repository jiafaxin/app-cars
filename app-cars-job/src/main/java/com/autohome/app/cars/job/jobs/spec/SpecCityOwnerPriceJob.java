package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.jiage.SpecCityOwnerPriceComponent;
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
 * @date 2024/3/1
 */
@JobHander("SpecCityOwnerPriceJob")
@Service
public class SpecCityOwnerPriceJob extends IJobHandler {

    @Autowired
    SpecCityOwnerPriceComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();

        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 500 : Integer.parseInt(params[0]);

        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
