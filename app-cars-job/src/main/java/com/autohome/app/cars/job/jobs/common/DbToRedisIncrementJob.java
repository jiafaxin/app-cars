package com.autohome.app.cars.job.jobs.common;


import com.autohome.app.cars.job.common.RoomType;
import com.autohome.app.cars.job.common.RunOn;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.services.dtos.DataSyncConfig;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *  增量从db往redis同步数据
 *  只有个从机房才会启动
 */
@JobHander("DbToRedisIncrementJob")
@Service
@Slf4j
@RunOn(type = RoomType.SLAVE)
public class DbToRedisIncrementJob extends IJobHandler implements ApplicationContextAware {

    @Override
    public ReturnT<String> execute(String... strings) {

        long s = System.currentTimeMillis();
        runAll(strings);
        long e = System.currentTimeMillis();
        XxlJobLogger.log("已完成同步，共用时：" + (e - s) / 1000 + "s");
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void runAll(String... strings){
        Map<String, BaseComponent> serviceMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, BaseComponent.class);

        String lockKey = "job:DbToRedisJob:lock"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+":v7:";

        int increment = 5 * 60;

        if(strings!=null && strings.length>0 && StringUtils.isNotBlank(strings[0])) {
            increment = Integer.parseInt(strings[0]);
        }


        String fileName = XxlJobFileAppender.contextHolder.get();

        int finalIncrement = increment;
        serviceMap.forEach((serviceName, serviceObj)-> {
            if (!stringRedisTemplate.opsForValue().setIfAbsent(lockKey + serviceName, "true", 5, TimeUnit.MINUTES)) {
                return;
            }

            long s = System.currentTimeMillis();
            XxlJobLogger.log("开始同步：" + serviceName);

            try {
                serviceObj.dbToRedis(finalIncrement,logInfo -> {
                    XxlJobFileAppender.contextHolder.set(fileName);
                    XxlJobLogger.log((String)logInfo);
                });
            } catch (Exception e) {
                log.error("db to redis 异常:" + serviceName, e);
                XxlJobLogger.log("db to redis 异常:" + serviceName, e);
            }
            XxlJobLogger.log("结束同步：" + serviceName + "，共耗时：" + (System.currentTimeMillis() - s));
        });
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
