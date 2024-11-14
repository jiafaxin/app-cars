package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 二手车入口信息:两个小时更新一次
 * 0 3 0/2 * * ?
 * 此服务：分片广播
 */
@JobHander("SeriesCityAskPriceNewJob")
@Service
public class SeriesCityAskPriceNewJob extends IJobHandler {

    @Autowired
    SeriesCityAskPriceNewComponent service;

    @Override
    public ReturnT<String> execute(String... strings) {
        String fileName = XxlJobFileAppender.contextHolder.get();

        CompletableFuture.runAsync(()->{
            service.refreshAll(x->{
                XxlJobFileAppender.contextHolder.set(fileName);
                XxlJobLogger.log(x);
            });
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
