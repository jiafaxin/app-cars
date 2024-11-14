package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.SpecSpecialConfigMapper;
import com.autohome.app.cars.mapper.popauto.entities.ConfigSpecificEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecSpecificConfigDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecificConfigInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RedisConfig
@Slf4j
@DBConfig(tableName = "spec_special_config")
public class SpecSpecialConfigComponent extends BaseComponent<SpecSpecificConfigDto> {

    private static String specIdParamName = "specId";

    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private SpecDetailComponent specDetailComponent;
    @Autowired
    private SpecSpecialConfigMapper specSpecialConfigMapper;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecSpecificConfigDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecSpecificConfigDto>> get(List<Integer> specIdList) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
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
                xxlLog.accept("specid=" + specId + " success");
            } catch (Exception e) {
                xxlLog.accept("specid = " + specId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
        });
    }

    public void refresh(int specId) {
        try {
            getData(specId);
        } catch (Exception e) {
            log.error("刷新车型特殊配置异常-exception:{}", e);
        }
    }

    private void getData(int specId){
        SpecDetailDto specDetailDto = specDetailComponent.get(specId).join();
        if(specDetailDto != null && SpecParamIsShow(specDetailDto)){
            List<SpecSpecificConfigDto.ConfigItem> configItems = getConfigItems(specId);
            SpecSpecificConfigDto dto = new SpecSpecificConfigDto();
            dto.setSpecid(specId);
            dto.setConfigitems(configItems);
            update(makeParam(specId), dto);
        }
    }

    /**
     * 返回信息查询组装
     * @param specId
     * @return
     */
    public List<SpecSpecificConfigDto.ConfigItem> getConfigItems(int specId){
        List<SpecSpecificConfigDto.ConfigItem> configItems = new ArrayList<>();
        Map<Integer, String> dictionary = new HashMap<>();
        dictionary.put(0, "-");
        dictionary.put(1, "●");
        dictionary.put(2, "○");
        if (specId < 1000000) {
            List<SpecificConfigInfo> specificBySpecIdList = getList(specId);
            specificBySpecIdList.forEach(specificConfigInfo -> {
                SpecSpecificConfigDto.ConfigItem configItem = new SpecSpecificConfigDto.ConfigItem();
                configItem.setBaikeid(specificConfigInfo.getBaiKeId());
                configItem.setBaikeurl(specificConfigInfo.getBaiKeUrl());
                configItem.setConfigid(specificConfigInfo.getItemId());
                configItem.setName(specificConfigInfo.getItemName());
                configItem.setSpecid(specificConfigInfo.getSpecId());
                configItem.setValue(dictionary.get(specificConfigInfo.getItemValue()));
                configItem.setPrice(specificConfigInfo.getPrice().compareTo(BigDecimal.ZERO) == 0 ? "" : specificConfigInfo.getPrice().toString());
                //返回参数组装
                configItems.add(configItem);
            });
        }
        return configItems;
    }

    private boolean SpecParamIsShow(SpecDetailDto specDetailDto){
        return specDetailDto.getState() == 40 || specDetailDto.getParamIsShow() == 1;
    }

    /**
     * 外部使用 同步
     * @param specId
     * @return
     */
    public List<SpecificConfigInfo> getList(int specId) {
        List<SpecificConfigInfo> specificConfigInfos = new ArrayList<>();
        List<ConfigSpecificEntity> configSpecificEntities = specSpecialConfigMapper.getConfigSpecificBySpecId(specId);
        List<SpecificConfigInfo> configInfoList = convert(configSpecificEntities);
        if(!CollectionUtils.isEmpty(configInfoList)){
            specificConfigInfos.addAll(configInfoList);
        }
        return specificConfigInfos.stream().sorted(Comparator.comparing(SpecificConfigInfo::getSort)).collect(Collectors.toList());
    }

    /**
     * 转换
     * @param configSpecificEntities
     * @return
     */
    private List<SpecificConfigInfo> convert(List<ConfigSpecificEntity> configSpecificEntities){
        if(CollectionUtils.isEmpty(configSpecificEntities)){
            return null;
        }
        List<SpecificConfigInfo> specificConfigInfos = new ArrayList<>();
        configSpecificEntities.forEach(configSpecificEntity -> {
            SpecificConfigInfo specificConfigInfo = new SpecificConfigInfo();
            specificConfigInfo.setItemId(configSpecificEntity.getItemId());
            specificConfigInfo.setItemName(configSpecificEntity.getItemName());
            specificConfigInfo.setBaiKeId(configSpecificEntity.getBaiKeId());
            specificConfigInfo.setBaiKeUrl(configSpecificEntity.getBaiKeUrl());
            specificConfigInfo.setSpecId(configSpecificEntity.getSpecId());
            specificConfigInfo.setItemValue(configSpecificEntity.getItemValue());
            specificConfigInfo.setPrice(configSpecificEntity.getPrice());
            specificConfigInfo.setSort(configSpecificEntity.getSort());
            specificConfigInfos.add(specificConfigInfo);
        });
        return specificConfigInfos;
    }

}
