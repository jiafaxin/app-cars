package com.autohome.app.cars.service.components.video;

import com.autohome.app.cars.apiclient.video.SpecSmallVideoApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.video.dtos.SpecConfigSmallVideoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RedisConfig(keyVersion = "v3")
@Slf4j
@DBConfig(tableName = "spec_config_small_video")
public class SpecConfigSmallVideoComponent extends BaseComponent<SpecConfigSmallVideoDto> {

    private static String paramName = "specId";

    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private SpecSmallVideoApiClient smallVideoApiClient;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(paramName, specId).build();
    }

    public CompletableFuture<SpecConfigSmallVideoDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecConfigSmallVideoDto>> get(List<Integer> specIds) {
        return baseGetListAsync(specIds.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    public CompletableFuture<Map<Integer,SpecConfigSmallVideoDto>> getMap(List<Integer> specIds) {
        return get(specIds).thenApply(x->{
            if(Objects.isNull(x)){
                return new HashMap<>();
            }
            x.removeIf(Objects::isNull);
            return x.stream().collect(Collectors.toMap(SpecConfigSmallVideoDto::getSpecId, v->v,(v1, v2)->v2));
        });
    }



    public void refreshAll(Consumer<String> xxlLog) {
        List<Integer> specIds = specMapper.getAllSpecIds();
        List<Integer> cvSpecIds = specMapper.getAllCvSpecIds();
        specIds.addAll(cvSpecIds);
        specIds = specIds.stream().distinct().collect(Collectors.toList());
        specIds.forEach(specId -> {
            try {
                getData(specId);
                ThreadUtil.sleep(50);
                xxlLog.accept(specId + " success ");
            } catch (Exception e) {
                xxlLog.accept(specId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
        });
    }

    public void refresh(int specId) {
        try {
            getData(specId);
        } catch (Exception e) {
            log.error("刷新车型配置小视频异常-exception:{}", e);
        }
    }

    private void getData(int specId){
        smallVideoApiClient.getConfigSmallVideoResult(String.valueOf(specId)).thenAccept(result -> {
            if (result != null && result.getResult() != null && ListUtil.isNotEmpty(result.getResult().getVideolist())) {
                SpecConfigSmallVideoDto dto = new SpecConfigSmallVideoDto();
                dto.setSpecId(specId);
                result.getResult().getVideolist().forEach(v->{
                    dto.getVideoInfoMap().put(v.getSecondcidoriginal(),v);
                });
                update(makeParam(specId), dto);
            }
        }).join();
    }

}
