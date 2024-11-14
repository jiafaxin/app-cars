package com.autohome.app.cars.job.jobs.file;

import com.autohome.app.cars.service.components.file.MegaDataComponent;
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
@JobHander("MegaFileJob")
@Service
public class FileJob extends IJobHandler {

    @Autowired
    MegaDataComponent fileComponent;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        fileComponent.setMegaPicData(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
