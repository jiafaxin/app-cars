package com.autohome.app.cars.job.jobs.speclist;

import com.autohome.app.cars.service.components.dealer.SeriesCityDealerIMComponent;
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
 * @description : 车系-车型列表IM数据job
 * @date : 2024/4/23 20:33
 */
@JobHander("SeriesCityDealerIMJob")
@Service
public class SeriesCityDealerIMJob extends IJobHandler {
    @Autowired
    SeriesCityDealerIMComponent seriesCityDealerIMComponent;

    @Override
    public ReturnT<String> execute(String... strings) {
        int totalMinutes = strings == null || strings.length == 0 || StringUtils.isBlank(strings[0]) ? 60 : Integer.parseInt(strings[0]);
        String fileName = XxlJobFileAppender.contextHolder.get();
        seriesCityDealerIMComponent.refreshAll(totalMinutes, x -> {
            XxlJobFileAppender.contextHolder.set(fileName);
            XxlJobLogger.log(x);
        });
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
