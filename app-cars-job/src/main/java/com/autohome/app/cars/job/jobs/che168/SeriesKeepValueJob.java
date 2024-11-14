package com.autohome.app.cars.job.jobs.che168;

import com.autohome.app.cars.service.components.che168.SeriesKeepValueComponent;
import com.autohome.app.cars.service.components.clubcard.SeriesClubCardHotComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 二手车保值率任务，一天更新一次
 */
@JobHander("SeriesKeepValueJob")
@Service
public class SeriesKeepValueJob extends IJobHandler {


    @Autowired
    SeriesKeepValueComponent seriesKeepValueComponent;


    @Override
    public ReturnT<String> execute(String... strings) {
        int totalMinutes = strings == null || strings.length == 0 || StringUtils.isBlank(strings[0]) ? 30 : Integer.parseInt(strings[0]);
        String fileName = XxlJobFileAppender.contextHolder.get();
        seriesKeepValueComponent.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }

}
