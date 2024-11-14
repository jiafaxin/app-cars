package com.autohome.app.cars.service.components.jiage;


import com.autohome.app.cars.apiclient.jiage.JiageApiClient;
import com.autohome.app.cars.apiclient.jiage.dtos.SpecCityOwnerPriceResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.che168.dtos.SpecCityUsedCarDto;
import com.autohome.app.cars.service.components.jiage.dtos.SpecCityListOwnerPriceDto;
import com.autohome.app.cars.service.components.jiage.dtos.SpecCityOwnerPriceListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * @date 2024/3/1
 */
@Component
@DBConfig(tableName = "spec_city_ownerprice")
public class SpecCityOwnerPriceComponent extends BaseComponent<SpecCityListOwnerPriceDto> {

    final static String specIdParamName = "specId";

    final static String cityParamName = "cityId";

    @SuppressWarnings("all")
    @Autowired
    JiageApiClient jiageApiClient;

    TreeMap<String, Object> makeParam(int specId, int cityId) {
        return BaseComponent.ParamBuilder.create(specIdParamName, specId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SpecCityListOwnerPriceDto> get(int specId, int cityId) {
        return baseGetAsync(makeParam(specId, cityId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        HashSet<String> newKeys = new HashSet<>();
        loopSpec(totalMinutes, (specId) -> jiageApiClient.getOwnerPriceCityList(specId + "")
                .thenAccept(data -> {
                    if (data == null || data.getReturncode() != 0) {
                        return;
                    }
                    if (data.getResult()!=null && !data.getResult().isEmpty()) {
                        data.getResult().forEach(item->{
                            SpecCityListOwnerPriceDto dto = new SpecCityListOwnerPriceDto();
                            dto.setSpecId(specId);
                            dto.setTotal(item.getPriceCount());
                            update(makeParam(specId, item.getCityId()), dto);
                            newKeys.add(getKey(makeParam(specId, item.getCityId())));
                        });
                    }
                }).exceptionally(e -> {
                    xxlLog.accept(specId + " - 车主价格条数 失败:" + ExceptionUtil.getStackTrace(e));
                    return null;
                }), xxlLog);

        deleteHistory(newKeys,xxlLog);
    }
}
