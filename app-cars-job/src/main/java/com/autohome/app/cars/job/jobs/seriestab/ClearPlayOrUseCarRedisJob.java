package com.autohome.app.cars.job.jobs.seriestab;

import com.autohome.app.cars.service.components.owner.SeriesPlayCarComponent;
import com.autohome.app.cars.service.components.owner.SeriesUseCarComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander("ClearPlayOrUseCarRedisJob")
@Service
public class ClearPlayOrUseCarRedisJob extends IJobHandler {
    @Autowired
    SeriesUseCarComponent useCarComponent;

    @Autowired
    SeriesPlayCarComponent playCarComponent;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        int totalMinutes = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 30 : Integer.parseInt(params[0]);

        if (totalMinutes == 1) {
            useCarComponent.refreshClearAll(x -> XxlJobLogger.log(x));
        } else if (totalMinutes == 2) {
            playCarComponent.refreshClearAll(x -> XxlJobLogger.log(x));
        } else {
            playCarComponent.refreshClearAll(x -> XxlJobLogger.log(x));
            useCarComponent.refreshClearAll(x -> XxlJobLogger.log(x));
        }

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
