package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.video.SpecShiCeSmallVideoComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lihongchen
 * @date 2024/5/15
 * 车型原创测评小视频
 * 每天2点执行一次
 * 0 0 2 1/1 * ?
 */
@JobHander("SpecShiCeSmallVideoJob")
@Service
public class SpecShiCeSmallVideoJob extends IJobHandler {

    @Autowired
    SpecShiCeSmallVideoComponent component;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        component.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
