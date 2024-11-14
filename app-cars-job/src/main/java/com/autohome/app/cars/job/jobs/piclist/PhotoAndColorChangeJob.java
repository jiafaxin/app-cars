package com.autohome.app.cars.job.jobs.piclist;

import com.autohome.app.cars.service.components.car.CarPhotoComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 监测颜色和图片修改
 *  0/30 * * * * ?
 */
@JobHander("PhotoAndColorChangeJob")
@Service
public class PhotoAndColorChangeJob extends IJobHandler {

    @Autowired
    CarPhotoComponent component;

    @Override
    public ReturnT<String> execute(String... strings) {

        String fileName = XxlJobFileAppender.contextHolder.get();
        component.refreshNew(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
