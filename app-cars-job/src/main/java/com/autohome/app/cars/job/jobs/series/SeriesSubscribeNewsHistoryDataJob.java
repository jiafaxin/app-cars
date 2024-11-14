package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.newcar.SubscribeNewsHistoryData;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zzli
 * @description : 动态频道历史数据
 * @date : 2024/9/9 15:43
 */
@JobHander("SubscribeNewsHistoryDataJob")
@Service
public class SeriesSubscribeNewsHistoryDataJob extends IJobHandler {
    @Autowired
    private SubscribeNewsHistoryData subscribeNewsHistoryData;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        int type = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 1 : Integer.parseInt(params[0]);

        XxlJobLogger.log(String.valueOf(type));

        String fileName = XxlJobFileAppender.contextHolder.get();

        if (type == 5) {
            subscribeNewsHistoryData.refreshMonthRank(x -> {
                XxlJobFileAppender.contextHolder.set(fileName);
                XxlJobLogger.log(x);
            });
        } else if (type == 6) {
            subscribeNewsHistoryData.refreshWeekRank(x -> {
                XxlJobFileAppender.contextHolder.set(fileName);
                XxlJobLogger.log(x);
            });
        } else if (type == 7) {
            subscribeNewsHistoryData.refreshPriceDown(x -> {
                XxlJobFileAppender.contextHolder.set(fileName);
                XxlJobLogger.log(x);
            });
        } else if (type == 1) {
            subscribeNewsHistoryData.refreshAll(x -> {
                XxlJobFileAppender.contextHolder.set(fileName);
                XxlJobLogger.log(x);
            });
        }

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }

}
