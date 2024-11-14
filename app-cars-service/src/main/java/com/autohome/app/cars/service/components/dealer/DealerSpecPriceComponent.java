package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerSpecPriceListResult;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecOutInnerColorDto;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@Slf4j
@DBConfig(tableName = "dealer_spec_price")
public class DealerSpecPriceComponent extends BaseComponent<DealerSpecPriceListResult> {

    @Autowired
    DealerApiClient dealerApiClient;


    final static String paramName = "dealerId";

    TreeMap<String, Object> makeParam(int dealerId) {
        return ParamBuilder.create(paramName, dealerId).build();
    }

    public CompletableFuture<DealerSpecPriceListResult> get(int dealerId) {
        return baseGetAsync(makeParam(dealerId));
    }

    public CompletableFuture<List<DealerSpecPriceListResult>> getList(List<Integer> dealerIds) {
        if (dealerIds == null || dealerIds.size() == 0) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        List<TreeMap<String, Object>> params = dealerIds.stream().map(x -> makeParam(x)).collect(Collectors.toList());
        return baseGetListAsync(params).thenApply(x -> {
            if (x == null) {
                return x;
            }
            x.removeIf(y -> y == null);
            return x;
        }).exceptionally(e -> {
            log.error("获取经销商信息报错", e);
            return new ArrayList<>();
        });
    }


    public String getKey(int dealerId) {
        return super.getKey(makeParam(dealerId));
    }


    public String get(TreeMap<String, Object> params) {
        DealerSpecPriceListResult dto = get((int) params.get("dealerId")).join();
        return JsonUtil.toString(dto);
    }

    public List<DealerSpecPriceListResult> mGet(List<Integer> specId) {
        return baseGetList(specId.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    public void updateBeatch(List<DealerSpecPriceListResult> items) {
        Map<TreeMap<String, Object>, DealerSpecPriceListResult> datas = new HashMap<>();
        for (DealerSpecPriceListResult item : items) {
            datas.put(makeParam(item.getDealerId()), item);
        }
        updateBatch(datas);
    }


    public void deleteHistorys(HashSet<String> newKeys,Consumer<String> xxlLog){
        deleteHistory(newKeys,xxlLog);
    }
}
