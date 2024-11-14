package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.misc.NewSeriesCityHotNewsAndTabComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangchengtao
 * @date 2024/8/22 15:06
 */
@JobHander("NewSeriesCityHotNewsAndTabJob")
@Service
public class NewSeriesCityHotNewsAndTabJob extends IJobHandler {

    @Resource
    private NewSeriesCityHotNewsAndTabComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 5 : Integer.parseInt(params[0]);
        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
