package com.autohome.app.cars.service.components.koubei;

import com.autohome.app.cars.apiclient.koubei.KoubeiApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.koubei.dtos.SpecKoubeiDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * @date 2024/2/29
 */
@Component
@DBConfig(tableName = "spec_koubei")
@RedisConfig(keyVersion = "v2")
public class SpecKouBeiComponent extends BaseComponent<SpecKoubeiDto> {

    static String paramName = "specId";

    @SuppressWarnings("all")
    @Autowired
    KoubeiApiClient koubeiApiClient;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(paramName, specId).build();
    }

    public CompletableFuture<SpecKoubeiDto> get(int specId) {
        return baseGetAsync(makeParam(specId));

    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSpec(totalMinutes, specId -> koubeiApiClient.getSpecScore(String.valueOf(specId))
                .thenAccept(data -> {
                    if (data == null || data.getReturncode() != 0) {
                        return;
                    }

                    if (data.getResult() == null || data.getResult().isEmpty()) {
                        delete(makeParam(specId));
                        return;
                    }

                    SpecKoubeiDto dto = new SpecKoubeiDto();
                    dto.setUserScore(data.getResult().get(0).getUserScore());
                    dto.setScoreUserNum(data.getResult().get(0).getScoreUserNum());
                    update(makeParam(specId), dto);
                }).exceptionally(e -> {
                    xxlLog.accept(specId + "失败:" + ExceptionUtil.getStackTrace(e));
                    return null;
                }).join(), xxlLog);
    }

}
