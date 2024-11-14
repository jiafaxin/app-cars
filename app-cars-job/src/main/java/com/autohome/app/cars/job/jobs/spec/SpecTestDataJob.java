package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.car.SpecSpecialConfigComponent;
import com.autohome.app.cars.service.components.car.SpecTestDataComponent;
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
 * @date 2024/6/21
 * 车型实测
 * 每天2点执行一次
 * 0 0 2 1/1 * ?
 */
@JobHander("SpecTestDataJob")
@Service
public class SpecTestDataJob extends IJobHandler {

    @Autowired
    SpecTestDataComponent component;

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
