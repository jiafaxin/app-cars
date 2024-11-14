package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.service.components.recrank.sale.RankNewEnergyNewPowerHotComponent;
import com.autohome.app.cars.service.components.uv.EsSeriesUvComponent;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@JobHander("EsSeriesUv")
@Service
public class EsSeriesUvJob extends IJobHandler {
    @Resource
    private EsSeriesUvComponent esSeriesUvComponent;

    @Resource
    private RankNewEnergyNewPowerHotComponent newPowerHotComponent;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        String fileName = XxlJobFileAppender.contextHolder.get();
        esSeriesUvComponent.refreshAll(x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
            // 刷新新势力热度榜缓存
            newPowerHotComponent.refreshCache(log -> {
                XxlJobFileAppender.contextHolder.set(fileName);
                XxlJobLogger.log(log);
            });
        });

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
