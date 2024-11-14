package com.autohome.app.cars.job.jobs.recrank;


import com.autohome.app.cars.service.components.recrank.sale.NewEnergyPowerConsumptionAndBatteryLifeComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 新能源榜-电耗榜&续航榜 数据定时任务
 */
@JobHander("NewEnergyPowerConsumptionAndBatteryLifeUpdateJob")
@Service
public class NewEnergyPowerConsumptionAndBatteryLifeUpdateJob extends IJobHandler {
    @Resource
    private NewEnergyPowerConsumptionAndBatteryLifeComponent service;

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
