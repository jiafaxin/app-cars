package com.autohome.app.cars.job.jobs.baike;

import com.autohome.app.cars.service.components.baike.BaikelinkforconfigComponent;
import com.autohome.app.cars.service.components.car.PicParamConfigComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander("BaikeJob")
@Service
public class BaikeJob extends IJobHandler {

    @Autowired
    BaikelinkforconfigComponent service;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        service.refreshAll(x->{
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
