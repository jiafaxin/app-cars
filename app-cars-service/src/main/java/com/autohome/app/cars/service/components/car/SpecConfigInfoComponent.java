package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.car.dtos.SpecConfigResult;
import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.carconfig.SpecElectric;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.KeyValueDto;
import com.autohome.app.cars.mapper.popauto.ConfigMapper;
import com.autohome.app.cars.mapper.popauto.SpecConfigMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.*;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecConfigInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RedisConfig
@DBConfig(tableName = "spec_config_info")
@Slf4j
public class SpecConfigInfoComponent extends BaseComponent<SpecConfigInfoDto> {

    private static String specIdParamName = "specId";
    //    @Autowired
//    private SpecConfigApiClient configApiClient;
    @Autowired
    SpecDetailComponent specDetailComponent;
    @Autowired
    ConfigMapper configMapper;
    @Autowired
    SpecConfigMapper specConfigMapper;
    @Autowired
    SpecMapper specMapper;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecConfigInfoDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecConfigInfoDto>> get(List<Integer> specIdList) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }


    public void refreshAll(int totalMinutes,Consumer<String> log) {

        // 所有车型
        List<SpecEntity> allSpecEntities = specMapper.getSpecAll();
        allSpecEntities.addAll(specMapper.getCvSpecAll());
        allSpecEntities = allSpecEntities.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Map<Integer, SpecEntity> specMap = allSpecEntities.stream().collect(Collectors.toMap(SpecEntity::getId, Function.identity(), (k1, k2) -> k1));
        loopSpec(totalMinutes,specId->{
            SpecEntity specEntity = specMap.get(specId);
            try {
                update(makeParam(specEntity.getId()),builder(specEntity));
            } catch (Exception e) {
                log.accept(specEntity.getId() + " fail:" + ExceptionUtil.getStackTrace(e));
            }
        },log);

    }

    public void refresh(int specId) {
        // 车型
        SpecEntity specEntity;
        if (Spec.isCvSpec(specId)) {
            specEntity = specMapper.getCvSpec(specId);
        } else {
            specEntity = specMapper.getSpec(specId);
        }
        update(makeParam(specEntity.getId()), builder(specEntity));
    }

    private SpecConfigInfoDto builder(SpecEntity entity) {
        int specId = entity.getId();
        SpecConfigInfoDto specConfigInfoDto = new SpecConfigInfoDto();
        specConfigInfoDto.setSpecId(specId);
        List<ConfigTypeBaseInfo> list = convertConfig(configMapper.getAllConfig());
        Map<Integer, String> itemValues = convert(configMapper.getAllConfigItemValues());
        Map<Integer, String> subItems = convert(configMapper.getConfigSubItems());
        List<SpecConfigRelationEntity> data = specConfigMapper.getSpecConfigRelations(specId);
        List<SpecConfigPriceEntity> priceItem = specConfigMapper.getSpecConfigPrice(specId);
        List<SpecConfigSubItemEntity> specSubItem = specConfigMapper.getSpecConfigSubItems(specId);


        Map<String, List<SpecConfigPriceEntity>> priceItemsMap = new LinkedHashMap<>();
        for (SpecConfigPriceEntity item : priceItem) {
            String key = specId + "-" + item.getItemId();
            if (!priceItemsMap.containsKey(key)) {
                priceItemsMap.put(key, new ArrayList<>());
            }
            priceItemsMap.get(key).add(item);
        }


        Map<String, List<SpecConfigSubItemEntity>> specSubItemsMap = new LinkedHashMap<>();

        for (SpecConfigSubItemEntity item : specSubItem) {
            String key = specId + "-" + item.getItemId();
            if (!specSubItemsMap.containsKey(key)) {
                specSubItemsMap.put(key, new ArrayList<>());
            }
            specSubItemsMap.get(key).add(item);
        }

        String defaultValue = itemValues.get(0);

        for (ConfigTypeBaseInfo baseInfo : list) {

            List<SpecConfigResult.Configitems> items = new ArrayList<>();

            if (baseInfo.getItems() == null) continue;

            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                if (Spec.isCvSpec(specId) && item.getCVIsShow() != 1) continue;
                if ((!Spec.isCvSpec(specId)) && item.getIsShow() != 1) continue;
                if (entity.getFuelType() == 4 && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId()))
                    continue;
                AtomicInteger currentConfigValueEqualNullNum = new AtomicInteger();
                List<SpecConfigResult.Valueitems> valueitems = new ArrayList<>();

                List<SpecConfigRelationEntity> values = data;
                String key = specId + "-" + item.getItemId();
                List<SpecConfigPriceEntity> prices = priceItemsMap.containsKey(key) ? priceItemsMap.get(key) : new ArrayList<>();//.stream().filter(x -> x.getSpecId() == specId && x.getItemId() == item.getItemId()).collect(Collectors.toList());
                SpecConfigResult.Valueitems valueItem = new SpecConfigResult.Valueitems();
                valueItem.setSpecid(specId);
                if (item.getDisplayType() == 0) {  //横排
                    SpecConfigRelationEntity relation = values == null ? null : values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);

                    String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                    if (prices != null && prices.size() > 0) {
                        for (SpecConfigPriceEntity price : prices) {
                            SpecConfigResult.Price priceBuilder = new SpecConfigResult.Price();
                            priceBuilder.setSubname(subItems.containsKey(price.getSubItemId()) ? subItems.get(price.getSubItemId()) : "");
                            priceBuilder.setPrice(price.getPrice() + "");
                            valueItem.getPrice().add(priceBuilder);
                        }
                    }
                    valueItem.setValue(relation == null ? defaultValue : strValue);
                    if (StringUtils.isBlank(strValue) || strValue.equals("-") || strValue.equals("0")) {
                        currentConfigValueEqualNullNum.addAndGet(1);
                    }
                } else if (item.getDisplayType() == 1) { //竖排
                    String skey = specId + "-" + item.getItemId();
                    List<SpecConfigSubItemEntity> specSubItemList = specSubItemsMap.containsKey(skey) ? specSubItemsMap.get(skey) : new ArrayList<>();// !specSubItems.containsKey(specId) ? null : specSubItems.get(specId).stream().filter(x -> x.getItemId() == item.getItemId()).collect(Collectors.toList());
                    if (specSubItemList == null || specSubItemList.size() == 0) {
                        currentConfigValueEqualNullNum.addAndGet(1);
                    } else {
                        Map<Integer, SpecConfigPriceEntity> priceMap = new LinkedHashMap<>();
                        for (SpecConfigPriceEntity price : prices) {
                            priceMap.put(price.getSubItemId(), price);
                        }
                        for (SpecConfigSubItemEntity specConfigSubItem : specSubItemList) {
                            SpecConfigPriceEntity price = priceMap.containsKey(specConfigSubItem.getSubItemId()) ? priceMap.get(specConfigSubItem.getSubItemId()) : null;//  prices.stream().filter(x -> x.getSubItemId() == specConfigSubItem.getSubItemId()).findFirst().orElse(null);
                            SpecConfigResult.Sublist subItem = new SpecConfigResult.Sublist();
                            subItem.setSubname(subItems.containsKey(specConfigSubItem.getSubItemId()) ? subItems.get(specConfigSubItem.getSubItemId()).replace("标配/选配", "") : "");
                            subItem.setSubvalue(specConfigSubItem.getSubValue());
                            subItem.setPrice(price == null ? 0 : price.getPrice());
                            subItem.setLogo(specConfigSubItem.getLogo());
                            subItem.setSubitemid(specConfigSubItem.getSubItemId());
                            valueItem.getSublist().add(subItem);
                        }
                    }
                    valueItem.setValue(valueItem.getSublist().size() > 0 ? "" : "-");
                }
                valueitems.add(valueItem);


                if (item.getDynamicShow() == 1 && currentConfigValueEqualNullNum.get() > 0)
                    continue;

                SpecConfigResult.Configitems itemBuilder = new SpecConfigResult.Configitems();
                itemBuilder.setConfigid(item.getItemId());
                itemBuilder.setName(item.getItemName());
                itemBuilder.setLogo(item.getLogo());
                itemBuilder.setDisptype(item.getDisplayType());
                itemBuilder.getValueitems().addAll(valueitems);
                items.add(itemBuilder);
            }
            if (items.size() > 0) {
                SpecConfigResult.Configtypeitems typeItem = new SpecConfigResult.Configtypeitems();
                typeItem.setName(baseInfo.getTypeName());
                typeItem.setGroupname(Spec.DicConfig_Group.containsKey(baseInfo.getTypeName()) ? Spec.DicConfig_Group.get(baseInfo.getTypeName()) : "");
                typeItem.getConfigitems().addAll(items);
                specConfigInfoDto.getConfigtypeitems().add(typeItem);
            }
        }


        return specConfigInfoDto;
    }


    List<ConfigTypeBaseInfo> convertConfig(List<ConfigItemEntity> list) {
        List<ConfigTypeBaseInfo> infos = new ArrayList<>();
        for (ConfigItemEntity item : list) {
            ConfigTypeBaseInfo baseInfo = infos.stream().filter(x -> x.getTypeId() == item.getTypeId()).findFirst().orElse(null);
            if (baseInfo == null) {
                baseInfo = new ConfigTypeBaseInfo();
                baseInfo.setItems(new ArrayList<>());
                baseInfo.setTypeId(item.getTypeId());
                baseInfo.setTypeName(item.getTypeName());
                infos.add(baseInfo);
            }
            baseInfo.getItems().add(new ConfigItemBaseInfo() {
                {
                    setItemId(item.getItemId());
                    setItemName(item.getItemName());
                    setDynamicShow(item.getDynamicShow());
                    setCVIsShow(item.getCVIsShow());
                    setIsShow(item.getIsShow());
                    setDisplayType(item.getDisplayType());
                    setLogo(item.getLogo());
                }
            });
        }
        return infos;
    }

    Map<Integer, String> convert(List<KeyValueDto<Integer, String>> list) {
        Map<Integer, String> result = new LinkedHashMap<>();
        for (KeyValueDto<Integer, String> item : list) {
            result.put(item.getKey(), item.getValue());
        }
        return result;
    }

//    @Override
//    protected SpecConfigInfoDto sourceData(TreeMap<String, Object> params) {
//        AtomicReference<SpecConfigInfoDto> ret = new AtomicReference<>(null);
//        int specId = (int) params.get(specIdParamName);
//        configApiClient.getSpecConfigInfoListByIds(String.valueOf(specId)).thenAccept(result -> {
//            if (result != null && result.getResult() != null && result.getResult().getConfigtypeitems() != null) {
//                SpecConfigInfoDto dto = new SpecConfigInfoDto();
//                dto.setSpecId(specId);
//                dto.setConfigtypeitems(result.getResult().getConfigtypeitems());
//                update(params, dto);
//                ret.set(dto);
//            }
//        }).join();
//        return ret.get();
//    }
//
//    @Override
//    protected Map<TreeMap<String, Object>, SpecConfigInfoDto> sourceDatas(List<TreeMap<String, Object>> paramsList) {
//        Map<TreeMap<String, Object>, SpecConfigInfoDto> ret = new LinkedHashMap<>();
//        List<Integer> specids = paramsList.stream().map(x -> (int) x.get(specIdParamName)).collect(Collectors.toList());
//
//        int batchSize = 5;
//        List<List<Integer>> groupedSpecIds = IntStream.range(0, specids.size())
//                .boxed()
//                .collect(Collectors.groupingBy(index -> index / batchSize))
//                .values()
//                .stream()
//                .map(indices -> indices.stream().map(specids::get).collect(Collectors.toList()))
//                .collect(Collectors.toList());
//
//        groupedSpecIds.forEach(ids -> {
//            CompletableFuture<SpecConfigResult> specConfigResultFuture = configApiClient.getSpecConfigInfoListByIds(StringUtils.join(ids, ","));
//            specConfigResultFuture.thenAccept(result -> {
//                if (result != null && result.getResult() != null && result.getResult().getConfigtypeitems() != null) {
//                    String json = JsonUtil.toString(result);
//                    specids.forEach(specId -> {
//                        SpecConfigResult temp = JsonUtil.toObject(json, SpecConfigResult.class);
//                        temp.getResult().getConfigtypeitems().forEach(configtypeitem -> {
//                            configtypeitem.getConfigitems().forEach(configitem -> {
//                                configitem.getValueitems().removeIf(valueitem -> valueitem.getSpecid() != specId);
//                            });
//                        });
//                        SpecConfigInfoDto dto = new SpecConfigInfoDto();
//                        dto.setSpecId(specId);
//                        dto.setConfigtypeitems(temp.getResult().getConfigtypeitems());
//                        ret.put(makeParam(specId), dto);
//                        update(makeParam(specId), dto);
//                    });
//                }
//            });
//            specConfigResultFuture.join();
//        });
//        return ret;
//    }

}
