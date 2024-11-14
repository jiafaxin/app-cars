package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.ListCshDealerByCityResult;
import com.autohome.app.cars.apiclient.dealer.dtos.SpecPriceItem;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.mapper.appcars.SpecDealerMinPriceMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecBaseInfoEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.dealer.dtos.DealerAndPrice;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@Slf4j
@DBConfig(tableName = "dealers")
public class DealerComponent extends BaseComponent<ListCshDealerByCityResult> {

    @Autowired
    DealerApiClient dealerApiClient;


    final static String paramName = "dealerId";

    TreeMap<String, Object> makeParam(int dealerId) {
        return ParamBuilder.create(paramName, dealerId).build();
    }

    public CompletableFuture<ListCshDealerByCityResult> get(int dealerId) {
        return baseGetAsync(makeParam(dealerId));
    }

    public CompletableFuture<List<ListCshDealerByCityResult>> getList(List<Integer> specIds) {
        if(specIds==null||specIds.size()==0){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        List<TreeMap<String, Object>> params = specIds.stream().map(x -> makeParam(x)).collect(Collectors.toList());
        return baseGetListAsync(params).thenApply(x->{
            if(x==null){
                return x;
            }
            x.removeIf(y->y==null);
            return x;
        }).exceptionally(e->{
            log.error("获取经销商信息报错",e);
            return new ArrayList<>();
        });
    }

    public void refreshAll(Consumer<String> xxlLog) {
        loopCity(10,cityId->{
            dealerApiClient.getDealersByCityId(cityId).thenAccept(dealers->{
                if(dealers!=null && dealers.getReturncode()==0 && dealers.getResult()!=null && dealers.getResult().size()>0){
                    for (ListCshDealerByCityResult item : dealers.getResult()) {
                        update(makeParam(item.getDealerId()),item);
                    }
                }
            });
        },xxlLog);
    }


}
