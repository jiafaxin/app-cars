package com.autohome.app.cars.job.jobs.piclist;

import com.autohome.app.cars.service.components.car.ColorComponent;
import com.autohome.app.cars.service.components.dealer.DealerComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 同步颜色
 * 0 0 0/1 * * ?
 */
@JobHander("ColorJob")
@Service
public class ColorJob extends IJobHandler {

    @Autowired
    ColorComponent component;

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
