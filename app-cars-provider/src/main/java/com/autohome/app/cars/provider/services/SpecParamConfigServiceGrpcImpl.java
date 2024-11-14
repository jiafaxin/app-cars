package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carcfg.DubboSpecParamConfigServiceTriple;
import autohome.rpc.car.app_cars.v1.carcfg.GetSpecParamConfigInfoRequest;
import autohome.rpc.car.app_cars.v1.carcfg.GetSpecParamConfigInfoResponse;
import com.autohome.app.cars.service.services.SpecParamConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@DubboService
@RestController
@Slf4j
public class SpecParamConfigServiceGrpcImpl extends DubboSpecParamConfigServiceTriple.SpecParamConfigServiceImplBase {

    @Autowired
    SpecParamConfigService specParamConfigService;

    @Override
    @GetMapping(value = "/carcfg/config/getspecparamconfiginfo", produces = "application/json;charset=utf-8")
    public GetSpecParamConfigInfoResponse getSpecParamConfigInfo(GetSpecParamConfigInfoRequest request) {
        GetSpecParamConfigInfoResponse response = specParamConfigService.getSpecParamConfigInfo(request);
        return response;
    }

}
