package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.newcar.SeriesSubscribeNewsComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangchengtao
 * @date 2024/8/14 15:09
 */
@JobHander("SeriesSubscribeNewsJob")
@Service
public class SeriesSubscribeNewsJob extends IJobHandler {

    @Autowired
    private SeriesSubscribeNewsComponent component;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        component.refreshAll(x->{
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }



}
