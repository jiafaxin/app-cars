package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SpecConfigBagMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecConfigBagEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecConfigBagDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecOutInnerColorDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RedisConfig
@Slf4j
@DBConfig(tableName = "spec_config_bag")
public class SpecConfigBagComponent extends BaseComponent<SpecConfigBagDto> {

    private static String specIdParamName = "specId";

    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private SpecConfigBagMapper specConfigBagMapper;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecConfigBagDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecConfigBagDto>> get(List<Integer> specIdList) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    public String get(TreeMap<String, Object> params) {
        SpecConfigBagDto dto = get((int) params.get("specId")).join();
        return JsonUtil.toString(dto);
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
                xxlLog.accept("specId = " + specId + " success");
            } catch (Exception e) {
                xxlLog.accept("specId = " + specId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
        });
    }

    public void refresh(int specId) {
        try {
            getData(specId);
        } catch (Exception e) {
            log.error("刷新车型配置包异常-exception:{}", e);
        }
    }

    private void getData(int specId){
        List<SpecConfigBagEntity> allBags = specConfigBagMapper.getBags(specId);
        if (!CollectionUtils.isEmpty(allBags)) {
            Map<Integer, List<SpecConfigBagEntity>> map = new LinkedHashMap<>();
            allBags.forEach(bag -> {
                if (!map.containsKey(bag.getSpecId())) {
                    map.put(bag.getSpecId(), new ArrayList<>());
                }
                map.get(bag.getSpecId()).add(bag);
            });
            map.forEach((k, v) -> {
                SpecConfigBagDto dto = new SpecConfigBagDto();
                dto.setSpecid(k);
                if (!CollectionUtils.isEmpty(v)) {
                    v.forEach(bag -> {
                        SpecConfigBagDto.ConfigBagValue value = new SpecConfigBagDto.ConfigBagValue();
                        value.setId(bag.getId());
                        value.setSpecid(bag.getSpecId());
                        value.setBagid(bag.getBagId());
                        value.setName(bag.getBagName());
                        value.setPrice(bag.getPrice());
                        value.setPricedesc(getPriceDesc(bag.getPrice()));
                        value.setDescription(StringUtils.trim(bag.getDescrip()));
                        if(StringUtils.isNotEmpty(bag.getImgs())){
                            value.setImgurl(StringUtils.substringBefore(bag.getImgs(),","));
                        }
                        dto.getConfigbags().add(value);
                    });
                }
                update(makeParam(k), dto);
            });
        }
    }

    public String getPriceDesc(int price) {
        String priceDesc = "";
        if (price == 0) {
            priceDesc = "免费";
        } else if (price == 1) {
            priceDesc = "暂无价格";
        } else {
            priceDesc = price + "元";
        }
        return priceDesc;
    }

}
