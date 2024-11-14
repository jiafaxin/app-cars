package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.sou.CustomizedCarComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 目前的定制车数据很少，最开始初始化一遍数据后，job任务就可以先停下，不用每天都去扫一遍
 *
 * @author chengjincheng
 * @date 2024/3/6
 */
@JobHander("SpecCustomizedCarJob")
@Service
public class SpecCustomizedCarJob extends IJobHandler {

    @Autowired
    CustomizedCarComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();

        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0])
                ? 60
                : Integer.parseInt(params[0]);

        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
