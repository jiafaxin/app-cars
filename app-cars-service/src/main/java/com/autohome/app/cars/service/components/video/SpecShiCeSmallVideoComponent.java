package com.autohome.app.cars.service.components.video;

import com.autohome.app.cars.apiclient.video.SpecSmallVideoApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.video.dtos.SpecShiCeVideoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RedisConfig
@Slf4j
@DBConfig(tableName = "spec_shice_small_video")
public class SpecShiCeSmallVideoComponent extends BaseComponent<SpecShiCeVideoDto> {

    private static String paramName = "specId";

    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private SpecSmallVideoApiClient smallVideoApiClient;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(paramName, specId).build();
    }

    public CompletableFuture<SpecShiCeVideoDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecShiCeVideoDto>> get(List<Integer> specIds) {
        return baseGetListAsync(specIds.stream().map(x -> makeParam(x)).collect(Collectors.toList())).thenApply(x -> {
            x.removeIf(y -> y == null);
            return x;
        });
    }

    public CompletableFuture<Map<Integer,SpecShiCeVideoDto>> getMap(List<Integer> specIds) {
        return get(specIds).thenApply(list->list.stream().collect(Collectors.toMap(SpecShiCeVideoDto::getSpecId,v->v,(v1,v2)->v2)));
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
            log.error("刷新车型实测小视频异常-exception:{}", e);
        }
    }

    private void getData(int specId){
        smallVideoApiClient.getShiCeSmallVideoResult(String.valueOf(specId)).thenAccept(result -> {
            if (result != null && result.getResult() != null && ListUtil.isNotEmpty(result.getResult())) {
                SpecShiCeVideoDto dto = new SpecShiCeVideoDto();
                dto.setSpecId(specId);
                dto.setVideoInfoList(result.getResult());
                update(makeParam(specId), dto);
            }
        }).join();
    }

}
