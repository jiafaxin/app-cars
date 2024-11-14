package com.autohome.app.cars.job.jobs.club;

import com.autohome.app.cars.service.components.club.SpecClubTabComponent;
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
 * @date 2024/8/20 13:52
 */
@JobHander("SpecClubTabJob")
@Service
public class SpecClubTagJob extends IJobHandler {

    @Resource
    private SpecClubTabComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 30 : Integer.parseInt(params[0]);

        String fileName = XxlJobFileAppender.contextHolder.get();
        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
