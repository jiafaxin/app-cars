package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.car.SpecSpecialConfigComponent;
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
 * @date 2024/5/21
 * 车型特色配置
 * 每2小时执行一次
 * 0 0 0/2 * * ?
 */
@JobHander("SpecSpecialConfigJob")
@Service
public class SpecSpecialConfigJob extends IJobHandler {

    @Autowired
    SpecSpecialConfigComponent component;

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
