package com.autohome.app.cars.service.components.sou;

import com.autohome.app.cars.apiclient.sou.CarSearchApiClient;
import com.autohome.app.cars.apiclient.sou.dtos.SouGoodsAndNewCarResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.sou.dtos.CustomizedCarDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * @date 2024/3/1
 */
@Component
@DBConfig(tableName = "spec_customized_car")
@Slf4j
public class CustomizedCarComponent extends BaseComponent<CustomizedCarDto> {

    static String paramName = "specId";

    @SuppressWarnings("all")
    @Autowired
    CarSearchApiClient carSearchApiClient;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(paramName, specId).build();
    }

    public CompletableFuture<CustomizedCarDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSpec(totalMinutes, specId -> carSearchApiClient.getSouGoodsAndNewCar(specId)
                .thenAccept(data -> {
                    if (data == null || data.getReturncode() != 0) {
                        return;
                    }
                    if (data.getResult() == null
                            || CollectionUtils.isEmpty(data.getResult().getList())
                            || Objects.isNull(data.getResult().getList().get(0))) {
                        delete(makeParam(specId));
                        return;
                    }

                    SouGoodsAndNewCarResult result = data.getResult();
                    CustomizedCarDto dto = new CustomizedCarDto();
                    dto.setId(result.getList().get(0).getId());
                    update(makeParam(specId), dto);
                }).exceptionally(e -> {
                    xxlLog.accept(specId + "失败:" + ExceptionUtil.getStackTrace(e));
                    return null;
                }).join(), xxlLog);
        System.out.println("1");
    }
}
