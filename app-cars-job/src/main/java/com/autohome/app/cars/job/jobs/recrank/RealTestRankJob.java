package com.autohome.app.cars.job.jobs.recrank;

import com.autohome.app.cars.service.components.recrank.realtest.PowerConsumptionAndEnduranceComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 实测榜job
 * @author zhangchengtao
 * @date 2024/9/24 21:48
 */
@JobHander("RealTestRankJob")
@Service
public class RealTestRankJob extends IJobHandler {
    @Autowired
    private PowerConsumptionAndEnduranceComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        service.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
