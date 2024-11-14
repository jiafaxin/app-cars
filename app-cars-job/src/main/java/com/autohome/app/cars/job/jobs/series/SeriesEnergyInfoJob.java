package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.car.SeriesEnergyInfoComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zzli
 * @description : 新能源车系相关电车数据，用于糖豆；
 *  0 0 0/2 * * ? 每两小时执行一次
 * @date : 2024/2/22 10:16
 */
@JobHander("SeriesEnergyInfoJob")
@Service
public class SeriesEnergyInfoJob extends IJobHandler {
    @Autowired
    SeriesEnergyInfoComponent seriesEnergyInfoComponent;

    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        seriesEnergyInfoComponent.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
