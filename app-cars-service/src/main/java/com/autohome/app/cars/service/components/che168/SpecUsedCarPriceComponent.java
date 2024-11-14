package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.che168.dtos.GetCarSpecPirceRangeResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.che168.dtos.SpecUsedCarPriceInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@DBConfig(tableName = "spec_usedcar_price")
public class SpecUsedCarPriceComponent extends BaseComponent<SpecUsedCarPriceInfo> {

    @SuppressWarnings("all")
    @Autowired
    Api2scautork2Client api2scautork2Client;

    final static String specIdParamName = "specId";

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecUsedCarPriceInfo> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecUsedCarPriceInfo>> getList(List<Integer> specIdList) {
        return baseGetListAsync(specIdList.stream().map(this::makeParam).collect(Collectors.toList()));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSpec(totalMinutes, (specId) -> {
            api2scautork2Client.getCarSpecPirceRange(specId,110100).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }

                if (data.getResult() == null || CollectionUtils.isEmpty(data.getResult())) {
                    delete(makeParam(specId));
                    return;
                }
                GetCarSpecPirceRangeResult rangeResult = data.getResult().get(0);
                if(StringUtils.isEmpty(rangeResult.getMinprice())){
                    return;
                }
                SpecUsedCarPriceInfo dto = new SpecUsedCarPriceInfo();
                dto.setSpecid(rangeResult.getSpecid());
                dto.setSpecurl(rangeResult.getSpecurl());
                dto.setSeriesurl(rangeResult.getSeriesurl());
                dto.setMinprice(Double.valueOf(rangeResult.getMinprice()));
                dto.setMaxprice(Double.valueOf(rangeResult.getMaxprice()));
                dto.setName(rangeResult.getName());
                update(makeParam(specId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(specId + "车型二手车价格区间 失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        }, xxlLog);
    }

}
