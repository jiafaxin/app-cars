package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityCpsInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * 车型cps信息组件
 */
@Component
@RedisConfig(timeout = 600)
//@DBConfig(tableName = "spec_city_cpsinfo")
public class SpecCityCpsInfoComponent extends BaseComponent<SpecCityCpsInfoDto> {

    private final static String specIdParamName = "specId";
    private final static String cityParamName = "cityId";

    @Autowired
    private DealerApiClient dealerApiClient;

    TreeMap<String, Object> makeParam(int specId, int cityId) {
        return ParamBuilder.create(specIdParamName, specId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SpecCityCpsInfoDto> get(int specId, int cityId) {
        return baseGetAsync(makeParam(specId, cityId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSpecCity(totalMinutes, (specId, cityId) -> {
            dealerApiClient.getSpecCityCpsInfo(cityId, String.valueOf(specId)).thenAccept(data -> {
                if (data != null && ListUtil.isNotEmpty(data.getResult())) {
                    data.getResult().forEach(x -> {
                        if (x != null) {
                            SpecCityCpsInfoDto dto = new SpecCityCpsInfoDto();
                            dto.setSpecId(specId);
                            dto.setCityId(cityId);
                            dto.setCpsInfo(x);
                            update(makeParam(specId, cityId), dto);
                        }
                    });
                }
            }).exceptionally(e -> {
                xxlLog.accept("specId:" + specId + " cityId:" + cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }).join();
        }, xxlLog);
    }

    @Override
    protected SpecCityCpsInfoDto sourceData(TreeMap<String, Object> params) {
        AtomicReference<SpecCityCpsInfoDto> res = new AtomicReference<>(null);
        Integer specId = (Integer) params.get(specIdParamName);
        Integer cityId = (Integer) params.get(cityParamName);
        dealerApiClient.getSpecCityCpsInfo(cityId, String.valueOf(specId)).thenApply(data -> {
            if (data != null && ListUtil.isNotEmpty(data.getResult())) {
                data.getResult().forEach(x -> {
                    if (x != null) {
                        SpecCityCpsInfoDto dto = new SpecCityCpsInfoDto();
                        dto.setCityId(cityId);
                        dto.setSpecId(specId);
                        dto.setCpsInfo(x);
                        update(makeParam(specId, cityId), dto);
                        res.set(dto);
                    }
                });
            }
            return res.get();
        }).exceptionally(e -> {
            return null;
        }).join();
        return null;
    }


}
