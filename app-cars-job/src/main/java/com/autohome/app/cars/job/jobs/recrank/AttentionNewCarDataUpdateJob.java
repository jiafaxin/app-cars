package com.autohome.app.cars.job.jobs.recrank;

import com.autohome.app.cars.service.services.RecRankService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chengjincheng
 * @date 2024/3/27
 */
@JobHander("AttentionNewCarDataUpdateJob")
@Service
public class AttentionNewCarDataUpdateJob extends IJobHandler {

    @Autowired
    RecRankService recRankService;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        recRankService.updateAttentionNewCarData(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        }, false);
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
