package com.autohome.app.cars.job.jobs.spec;

import com.autohome.app.cars.service.components.car.SpecOutInnerColorComponent;
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
 * @date 2024/7/12
 * 车型外观内饰颜色
 * 每2小时执行一次
 * 0 0 0/2 * * ?
 */
@JobHander("SpecOutInnerColorJob")
@Service
public class SpecOutInnerColorJob extends IJobHandler {

    @Autowired
    SpecOutInnerColorComponent component;

    @Override
    public ReturnT<String> execute(String... params) {
        String fileName = XxlJobFileAppender.contextHolder.get();
        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0])
                ? 30
                : Integer.parseInt(params[0]);
        component.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }

//    @Override
//    public ReturnT<String> execute(String... params) throws Exception {
//        String fileName = XxlJobFileAppender.contextHolder.get();
//        component.refreshAll(x -> {
//            XxlJobFileAppender.contextHolder.set(fileName);
//            XxlJobLogger.log(x);
//        });
//        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
//    }

}
