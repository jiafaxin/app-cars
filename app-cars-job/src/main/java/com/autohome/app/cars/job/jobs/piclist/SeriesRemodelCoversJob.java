package com.autohome.app.cars.job.jobs.piclist;

import com.autohome.app.cars.service.components.cms.AutoShowConfigComponent;
import com.autohome.app.cars.service.components.remodel.SeriesRemodelCoversComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 改装封面图列表 2个小时执行一次
 * 0 0 0/2 * * ?
 */
@JobHander("SeriesRemodelCoversJob")
@Service
public class SeriesRemodelCoversJob extends IJobHandler {

    @Autowired
    SeriesRemodelCoversComponent component;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        component.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
