package com.autohome.app.cars.job.config;

import com.autohome.app.cars.job.common.RoomType;
import com.autohome.app.cars.job.common.RunOn;
import com.autohome.app.cars.service.services.dtos.DataSyncConfig;
import com.autohome.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class JobExecutionAspect {

    @Value("${CLUSTER_NAME:aurora}")
    String podEnvName;

    @Value("${autohome.job.mustBeExecuted:false}")
    boolean mustBeExecuted;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.DataSyncConfig).createFromJson('${data_sync_config:}')}")
    DataSyncConfig dataSyncConfig;

    @Around("execution(* com.autohome.app.cars.job.jobs..*.execute(..))")
    public Object aroundJobExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        if(mustBeExecuted){
            return joinPoint.proceed();
        }

        RunOn runOn = joinPoint.getTarget().getClass().getAnnotation(RunOn.class);
        //在所有机房运行
        if(runOn != null && runOn.equals(RoomType.All)){
            return joinPoint.proceed();
        }

        if(podEnvName.equals(dataSyncConfig.getJobCluster())){
            //主机房
            if(runOn == null || runOn.type().equals(RoomType.MASTER)){
                return joinPoint.proceed();
            }
            return new ReturnT<>(ReturnT.SUCCESS_CODE, "主机房不运行从机房job "+podEnvName+"-"+dataSyncConfig.getJobCluster());
        }else{
            //从机房
            if(runOn!=null && runOn.type().equals(RoomType.SLAVE)){
                log.error("开始执行从机房任务");
                return joinPoint.proceed();
            }
            return new ReturnT<>(ReturnT.SUCCESS_CODE, "从机房不运行主机房job "+podEnvName+"-"+dataSyncConfig.getJobCluster());
        }
    }
}