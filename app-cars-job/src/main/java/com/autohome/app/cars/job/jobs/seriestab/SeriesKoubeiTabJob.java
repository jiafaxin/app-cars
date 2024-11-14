package com.autohome.app.cars.job.jobs.seriestab;

import com.autohome.app.cars.service.components.dealer.SpecChannelComponent;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiComponent;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiTabComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 口碑标签和内容
 * 定时执行：每15分钟
 * * 0/15 * * * ?
 */
@JobHander("SeriesKoubeiTabJob")
@Service
public class SeriesKoubeiTabJob extends IJobHandler {

    @Autowired
    SeriesKouBeiTabComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 15 : Integer.parseInt(params[0]);
        
        service.refreshOne(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        }, totalMinutes);

        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}