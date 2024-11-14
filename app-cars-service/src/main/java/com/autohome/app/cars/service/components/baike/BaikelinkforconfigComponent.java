package com.autohome.app.cars.service.components.baike;

import com.autohome.app.cars.apiclient.baike.BaikeClient;
import com.autohome.app.cars.apiclient.baike.dtos.ConfigBaikeLinkDto;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.BrandSeriesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class BaikelinkforconfigComponent extends BaseComponent<List<ConfigBaikeLinkDto>> {

    @Autowired
    BaikeClient baikeClient;

    public CompletableFuture<Map<String,ConfigBaikeLinkDto>> getMap() {
        return baseGetAsync(null).thenApply(x -> {
            if (x == null || x.size() == 0) {
                return new HashMap<>();
            }
            return x.stream().collect(Collectors.toMap(ConfigBaikeLinkDto::getName,y->y));
        });
    }

    public List<ConfigBaikeLinkDto> getList() {
        return baseGet(null);
    }

    public void refreshAll(Consumer<String> xxlLog) {
        String regularTtemp = "^$0\\[$0汽$0车$0之$0家$0百$0科$0]$0";
        String regular = regularTtemp.replace("$0", "[\\u00A0\\u0020]*");
        baikeClient.getSeriesSortList().thenAccept(x->{
            if(x==null||x.getReturncode()!=0 ||x.getResult()==null||x.getResult().size()==0){
                delete(null);
            }else{
                for (ConfigBaikeLinkDto linkDto : x.getResult()) {
                    linkDto.setFirstpartcnt(linkDto.getFirstpartcnt().replaceAll(regular,""));
                }
                update(null, x.getResult());
            }
        });
    }

}
