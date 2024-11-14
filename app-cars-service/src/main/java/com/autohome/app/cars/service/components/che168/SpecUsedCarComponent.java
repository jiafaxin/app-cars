package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.che168.dtos.SpecUsedCarInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
@DBConfig(tableName = "spec_usedcar")
public class SpecUsedCarComponent extends BaseComponent<SpecUsedCarInfo> {

    @SuppressWarnings("all")
    @Autowired
    Api2scautork2Client api2scautork2Client;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    final static String specIdParamName = "specId";

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecUsedCarInfo> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSpec(totalMinutes, specId -> {
            SpecDetailDto specDetailDto = specDetailComponent.get(specId).join();
            if (specDetailDto == null) {
                return;
            }
            api2scautork2Client.getSpecUsedCarsJumpInfo(specDetailDto.getSeriesId(), specId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }

                if (data.getResult() == null || "暂无报价".equals(data.getResult().getSubtitle())) {
                    delete(makeParam(specId));
                    return;
                }
                SpecUsedCarInfo dto = new SpecUsedCarInfo();
                dto.setJumpUrl(data.getResult().getJumpurl());
                dto.setSubTitle(data.getResult().getSubtitle());
                dto.setTitle(data.getResult().getTitle());

                update(makeParam(specId), dto);
                xxlLog.accept(specId + "成功");
            }).exceptionally(e -> {
                xxlLog.accept(specId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        }, xxlLog);
    }

}
