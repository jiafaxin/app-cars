package com.autohome.app.cars.job.jobs.recrank;

import com.autohome.app.cars.service.components.recrank.sale.history.BrandRankHistoryComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zzli
 * @description : 品牌月榜销量趋势
 * @date : 2024/10/30 19:51
 */
@JobHander("BrandRankHistoryJob")
@Service
public class BrandRankHistoryJob extends IJobHandler {

    @Autowired
    BrandRankHistoryComponent service;

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
