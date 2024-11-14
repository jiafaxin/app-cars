package com.autohome.app.cars.service.components.video;

import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.EpibolyAiVideoMapper;
import com.autohome.app.cars.mapper.popauto.VideoPointLocationMapper;
import com.autohome.app.cars.mapper.popauto.entities.EpibolyAiVideoOrderDetailEntity;
import com.autohome.app.cars.mapper.popauto.entities.EpibolyAiVideoOrderEntity;
import com.autohome.app.cars.mapper.popauto.entities.PointParamConfigEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.video.dtos.SpecAiVideoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RedisConfig(keyVersion = "v2")
@Slf4j
@DBConfig(tableName = "spec_ai_video")
public class SpecAiVideoComponent extends BaseComponent<SpecAiVideoDto> {

    private static String paramName = "specId";

    @Resource
    private EpibolyAiVideoMapper epibolyAiVideoMapper;
    @Resource
    private VideoPointLocationMapper videoPointLocationMapper;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(paramName, specId).build();
    }

    public CompletableFuture<SpecAiVideoDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecAiVideoDto>> get(List<Integer> specIds) {
        return baseGetListAsync(specIds.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    public CompletableFuture<Map<Integer,SpecAiVideoDto>> getMap(List<Integer> specIds) {
        return get(specIds).thenApply(x->{
            if(Objects.isNull(x)){
                return new HashMap<>();
            }
            x.removeIf(Objects::isNull);
           return x.stream().collect(Collectors.toMap(SpecAiVideoDto::getSpecId, v->v,(v1, v2)->v2));
        });
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<Integer> seriesIds = seriesMapper.getAllSeriesIds();
        seriesIds.forEach(seriesId -> {
            try {
                List<SpecAiVideoDto.SpecAiVideoResult> specAiVideoDtoList = getConfigWithAiVideoForApp(seriesId);
                if(!CollectionUtils.isEmpty(specAiVideoDtoList)){
                    specAiVideoDtoList.stream().collect(Collectors.groupingBy(SpecAiVideoDto.SpecAiVideoResult::getSpecid)).forEach((specId, items) -> {
                        SpecAiVideoDto dto = new SpecAiVideoDto();
                        dto.setSpecId(specId);
                        items.forEach(v->{
                            dto.getVideoInfoMap().put(v.getConfigid(),v);
                        });
                        update(makeParam(specId), dto);
                        xxlLog.accept("specId=" + specId + " success ");
                    });
                }
                ThreadUtil.sleep(50);
                xxlLog.accept(seriesId + " success ");
            } catch (Exception e) {
                xxlLog.accept(seriesId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
        });
    }

    private List<SpecAiVideoDto.SpecAiVideoResult> getConfigWithAiVideoForApp(int seriesId) {
        if (seriesId <= 0) {
            return null;
        }
        List<SpecAiVideoDto.SpecAiVideoResult> specAiVideoDtoList = new ArrayList<>();
        //根据车系id获取审核通过的订单信息
        EpibolyAiVideoOrderEntity aiVideoOrderEntity = getBySeriesId(seriesId);
        if(null == aiVideoOrderEntity){
            return specAiVideoDtoList;
        }
        //获取智能类视频关联的配置信息
        List<PointParamConfigEntity> paramConfigEntities = getByBuId(4);
        if(CollectionUtils.isEmpty(paramConfigEntities)){
            return specAiVideoDtoList;
        }
        //只要配置信息
        paramConfigEntities = paramConfigEntities.stream().filter(pointParamConfigEntity -> pointParamConfigEntity.getDataType() == 2).collect(Collectors.toList());
        //根据订单id订单的视频详情信息
        List<EpibolyAiVideoOrderDetailEntity> orderDetailEntityList = getByOrderId(aiVideoOrderEntity.getOrderId());
        //返回信息组装
        for(PointParamConfigEntity pointParamConfigEntity : paramConfigEntities){
            SpecAiVideoDto.SpecAiVideoResult result = new SpecAiVideoDto.SpecAiVideoResult();
            result.setSeriesid(seriesId);
            result.setSpecid(aiVideoOrderEntity.getSpecId());
            result.setConfigid(pointParamConfigEntity.getParamConfigId());
            result.setConfigname(null != pointParamConfigEntity.getParamConfigName() ? pointParamConfigEntity.getParamConfigName() : "");
            //获取当前配置项点位的智能视频信息
            EpibolyAiVideoOrderDetailEntity orderDetailEntity = org.apache.dubbo.common.utils.CollectionUtils.isEmpty(orderDetailEntityList) ? null :
                    orderDetailEntityList.stream().filter(video -> video.getStatus() == 1 && video.getPointId() == pointParamConfigEntity.getPointLocationId()).findFirst().orElse(null);
            if(null == orderDetailEntity){
                continue;
            }else{
                result.setVideoid(orderDetailEntity.getSourceId());
            }
            specAiVideoDtoList.add(result);
        }
        return specAiVideoDtoList;
    }

    private EpibolyAiVideoOrderEntity getBySeriesId(int seriesId){
        List<EpibolyAiVideoOrderEntity> videoOrderEntityList = epibolyAiVideoMapper.getEpibolyAiVideoOrderAll();
        if(!CollectionUtils.isEmpty(videoOrderEntityList)){
            EpibolyAiVideoOrderEntity orderEntity = videoOrderEntityList.stream().filter(epibolyAiVideoOrderEntity ->
                    epibolyAiVideoOrderEntity.getSeriesId() == seriesId && epibolyAiVideoOrderEntity.getOrderStatus() == 2).findFirst().orElse(null);
            return orderEntity;
        }
        return null;
    }

    private List<PointParamConfigEntity> getByBuId(int buId){
        List<PointParamConfigEntity> pointParamConfigEntities = videoPointLocationMapper.getPointRelationParamConfigByBuId(buId);
        if(CollectionUtils.isEmpty(pointParamConfigEntities)){
            return null;
        }
        return pointParamConfigEntities;
    }

    private List<EpibolyAiVideoOrderDetailEntity> getByOrderId(int orderId){
        List<EpibolyAiVideoOrderDetailEntity> orderDetailEntityList = epibolyAiVideoMapper.getEpibolyAiVideoOrderDetailByOrderId(orderId);
        if(CollectionUtils.isEmpty(orderDetailEntityList)){
            return null;
        }
        return orderDetailEntityList;
    }


}
