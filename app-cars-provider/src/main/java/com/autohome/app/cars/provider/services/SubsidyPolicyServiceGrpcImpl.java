package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.subsidy.DubboSubsidyPolicyServiceTriple;
import autohome.rpc.car.app_cars.v1.subsidy.SubsidyPolicyReportRequest;
import autohome.rpc.car.app_cars.v1.subsidy.SubsidyPolicyReportResponse;
import com.autohome.app.cars.service.services.SubsidyPolicyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@DubboService
@RestController
@Slf4j
public class SubsidyPolicyServiceGrpcImpl extends DubboSubsidyPolicyServiceTriple.SubsidyPolicyServiceImplBase {

    @Autowired
    private SubsidyPolicyService subsidyPolicyService;

    @Override
    @GetMapping(value = "/subsidy/policy/report", produces = "application/json;charset=utf-8")
    public SubsidyPolicyReportResponse getSubsidyPolicyReport(SubsidyPolicyReportRequest request) {
        try {
            SubsidyPolicyReportResponse.Result.Builder resultBuilder =
                    subsidyPolicyService.getResult(request.getSeriesid(),
                            request.getSpecid(),
                            request.getCityid(),
                            request.getEid(),
                            request.getBusinessid(),
                            request.getOrdertype()).join();
            if (Objects.nonNull(resultBuilder)) {
                return SubsidyPolicyReportResponse.newBuilder()
                        .setReturnCode(0)
                        .setReturnMsg("success")
                        .setResult(resultBuilder)
                        .build();
            } else {
                return SubsidyPolicyReportResponse.newBuilder()
                        .setReturnCode(0)
                        .setReturnMsg("无补贴数据")
                        .build();
            }
        } catch (Exception e) {
            log.error("获取补贴信息异常", e);
            return SubsidyPolicyReportResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("获取补贴信息异常")
                    .build();
        }
    }

}