package com.autohome.app.cars.job.jobs.series;

import com.autohome.app.cars.common.enums.SeriesSubscribeNewsEnum;
import com.autohome.app.cars.service.components.newcar.SeriesSubscribeNewsComponent;
import com.autohome.app.cars.service.components.newcar.SubscribeNewsHistoryData;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhangchengtao
 * @date 2024/9/30 15:22
 */
@Slf4j
@JobHander("SeriesSubscribeNewsDataCleanJob")
@Service
public class SeriesSubscribeNewsDataCleanJob extends IJobHandler {

    @Autowired
    private SubscribeNewsHistoryData subscribeNewsHistoryData;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        int type = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 1 : Integer.parseInt(params[0]);
        XxlJobLogger.log(String.valueOf(type));
        String fileName = XxlJobFileAppender.contextHolder.get();
        SeriesSubscribeNewsEnum[] values = SeriesSubscribeNewsEnum.values();
        Optional<SeriesSubscribeNewsEnum> any = Arrays.stream(values).filter(x -> x.getType() == type).findAny();
        XxlJobFileAppender.contextHolder.set(fileName);
        if (any.isEmpty()) {
            XxlJobLogger.log("类型非法:" + type);
            return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
        }
        int count = subscribeNewsHistoryData.cleanData(type);
        if (count > 0) {
            XxlJobLogger.log("成功清理 [" + any.get().getDesc() + "] 类型 " + count + " 条数据");
        } else {
            XxlJobLogger.log("没有需要清理的数据!");
        }
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
