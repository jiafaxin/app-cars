package com.autohome.app.cars.job.jobs.piclist;

import com.autohome.app.cars.service.components.car.SeriesSixtyPicComponent;
import com.autohome.app.cars.service.components.remodel.SeriesRemodel3DComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 车系60点位车型图 30分钟执行一次
 * 0 0/30 * * * ?
 */
@JobHander("SeriesSixtyPicJob")
@Service
public class SeriesSixtyPicJob extends IJobHandler {

    @Autowired
    SeriesSixtyPicComponent component;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        int totalMinutes = strings == null || strings.length == 0 || StringUtils.isBlank(strings[0]) ? 60 : Integer.parseInt(strings[0]);
        component.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
