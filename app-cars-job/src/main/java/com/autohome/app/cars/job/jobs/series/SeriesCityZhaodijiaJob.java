package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.owner.SeriesCityZhaodijiaComponent;
import com.autohome.app.cars.service.components.owner.SeriesGaizhuangComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 更新价格信息:一个小时更新一次
 * 0 10 0/1 * * ?
 */
@JobHander("SeriesCityZhaodijiaJob")
@Service
public class SeriesCityZhaodijiaJob extends IJobHandler {

    @Autowired
    SeriesCityZhaodijiaComponent service;

    @Override
    public ReturnT<String> execute(String... strings) {
        int totalMinutes = strings==null||strings.length==0 || StringUtils.isBlank(strings[0]) ?500:Integer.parseInt(strings[0]);
        String fileName = XxlJobFileAppender.contextHolder.get();
        service.refreshAll(totalMinutes,x->{
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
