package com.autohome.app.cars.job.jobs.piclist;

import com.autohome.app.cars.service.components.car.CarPhotoComponent;
import com.autohome.app.cars.service.components.misc.SeriesCityTabComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 同步图片
 *  0 0 1/2 * * ?
 */
@JobHander("SeriesPhotoJob")
@Service
public class SeriesPhotoJob extends IJobHandler {

    @Autowired
    CarPhotoComponent service;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();

        int totalMinutes = strings == null || strings.length == 0 || StringUtils.isBlank(strings[0]) ? 500 : Integer.parseInt(strings[0]);

        service.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
