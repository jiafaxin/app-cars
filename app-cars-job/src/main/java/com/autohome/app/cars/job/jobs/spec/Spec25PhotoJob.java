package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.car.Spec25PhotoComponent;
import com.autohome.app.cars.service.components.car.SpecPicInfoComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@JobHander("Spec25PhotoJob")
@Service
public class Spec25PhotoJob extends IJobHandler {
    
    @Autowired
    Spec25PhotoComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();

        service.refreshAllNew(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
