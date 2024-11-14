package com.autohome.app.cars.job.jobs.recrank;

import com.autohome.app.cars.service.components.recrank.koubei.KoubeiRankComponent;
import com.autohome.app.cars.service.components.recrank.moto.MotoRankComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chengjincheng
 * @date 2024/7/19
 */
@JobHander("MotoRankJob")
@Service
public class MotoRankJob extends IJobHandler {

    @Autowired
    MotoRankComponent service;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        service.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
