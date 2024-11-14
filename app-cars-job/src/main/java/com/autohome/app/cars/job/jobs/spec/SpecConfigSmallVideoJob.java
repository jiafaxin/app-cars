package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.video.SpecConfigSmallVideoComponent;
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
 * @date 2024/5/14
 * 车型配置小视频
 * 每小时执行一次
 * 0 0 0/1 * * ?
 */
@JobHander("SpecConfigSmallVideoJob")
@Service
public class SpecConfigSmallVideoJob extends IJobHandler {

    @Autowired
    SpecConfigSmallVideoComponent component;

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
