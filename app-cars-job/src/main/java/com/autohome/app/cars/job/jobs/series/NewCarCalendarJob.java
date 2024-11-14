package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.newcar.NewCarCalendarComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zzli
 * @description : 新车日历-11.63.3版本的
 * @date : 2024/6/11 13:09
 */
@JobHander("NewCarCalendarJob")
@Service
public class NewCarCalendarJob extends IJobHandler {
    @Autowired
    NewCarCalendarComponent newCarCalendarComponent;

    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        newCarCalendarComponent.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
