package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.car.SeriesTimeAxisComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zzli
 * @description : 新车日历
 * @date : 2024/4/26 19:05
 */
@JobHander("SeriesTimeAxisJob")
@Service
public class SeriesTimeAxisJob extends IJobHandler {
    @Autowired
    SeriesTimeAxisComponent seriesTimeAxisComponent;

    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        seriesTimeAxisComponent.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
