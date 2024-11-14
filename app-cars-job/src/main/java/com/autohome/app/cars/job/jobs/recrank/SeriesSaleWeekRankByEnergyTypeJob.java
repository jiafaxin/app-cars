package com.autohome.app.cars.job.jobs.recrank;

import com.autohome.app.cars.service.components.recrank.sale.history.SeriesSaleWeekRankByEnergyTypeComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dx on 2024/7/9
 * 按能源类型汇总车系周销量排名数据job
 */
@JobHander("SeriesSaleWeekRankByEnergyTypeJob")
@Service
public class SeriesSaleWeekRankByEnergyTypeJob extends IJobHandler {
    @Autowired
    private SeriesSaleWeekRankByEnergyTypeComponent service;

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
