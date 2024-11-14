package com.autohome.app.cars.service.components.car;

import autohome.rpc.car.app_cars.v1.carcfg.Item;
import com.autohome.app.cars.apiclient.car.dtos.SpecConfigResult;
import com.autohome.app.cars.apiclient.testdata.dtos.TestStandardResult;
import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.carconfig.SpecElectric;
import com.autohome.app.cars.common.enums.TestDataConfigItemEnum;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.KeyValueDto;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.common.utils.PriceUtil;
import com.autohome.app.cars.mapper.popauto.ConfigMapper;
import com.autohome.app.cars.mapper.popauto.SpecConfigMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.*;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecTestDataDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecConfigInfoDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecParamConfigDto;
import io.netty.util.internal.StringUtil;
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
@DBConfig(tableName = "spec_config_info_new")
@Slf4j
public class SpecConfigInfoNewComponent extends BaseComponent<SpecParamConfigDto> {

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
    @Autowired
    private SpecTestDataComponent specTestDataComponent;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecParamConfigDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecParamConfigDto>> get(List<Integer> specIdList) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    public CompletableFuture<Map<Integer,SpecParamConfigDto>> getMap(List<Integer> specIdList) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x)).collect(Collectors.toList())).thenApply(result -> {
            if(Objects.isNull(result)){
                return new HashMap<>();
            }
            result.removeIf(x -> x == null);
            return result.stream().collect(Collectors.toMap(SpecParamConfigDto::getSpecId,item->item,(v1,v2)->v2));
        });
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

    private SpecParamConfigDto builder(SpecEntity entity) {
        int specId = entity.getId();
        SpecParamConfigDto specParamConfigDto = new SpecParamConfigDto();
        specParamConfigDto.setSpecId(specId);
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

        SpecTestDataDto specTestDataDto = specTestDataComponent.getSync(specId);
        Map<String, String> configtype = new HashMap<>();
        configtype.put("主动安全", "实测主动安全");
        configtype.put("驾驶功能", "实测智能驾驶");
        configtype.put("智能化配置", "实测智能座舱");
        configtype.put("被动安全", "");

        Map<String, String> testTab = new HashMap<>();
        testTab.put("主动安全", "安全性");
        testTab.put("驾驶功能", "智能驾驶");
        testTab.put("智能化配置", "智能座舱");

        Map<String, String> testTabId = new HashMap<>();
        testTabId.put("主动安全", "4");
        testTabId.put("驾驶功能", "7");
        testTabId.put("智能化配置", "8");
        for (ConfigTypeBaseInfo baseInfo : list) {
            if (baseInfo.getItems() == null){
                continue;
            }

            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                if (Spec.isCvSpec(specId) && item.getCVIsShow() != 1){
                    continue;
                }
                if ((!Spec.isCvSpec(specId)) && item.getIsShow() != 1){
                    continue;
                }
                if (entity.getFuelType() == 4 && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId())){
                    continue;
                }

                AtomicInteger currentConfigValueEqualNullNum = new AtomicInteger();
                String key = specId + "-" + item.getItemId();
                List<SpecConfigPriceEntity> prices = priceItemsMap.containsKey(key) ? priceItemsMap.get(key) : new ArrayList<>();//.stream().filter(x -> x.getSpecId() == specId && x.getItemId() == item.getItemId()).collect(Collectors.toList());

                SpecParamConfigDto.Item typeItem = new SpecParamConfigDto.Item();
                typeItem.setName(item.getItemName());
                typeItem.setLogo(item.getLogo());
                if (item.getDisplayType() == 0) {  //横排
                    SpecConfigRelationEntity relation = data == null ? null : data.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);

                    String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                    if (prices != null && prices.size() > 0) {
                        typeItem.setPriceinfo(StringUtils.join(prices.stream().map(i->PriceUtil.getPriceInfoNoDefult(i.getPrice())).collect(Collectors.toList())," / "));
                    }
                    typeItem.setValue(relation == null ? defaultValue : strValue);
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
                            SpecParamConfigDto.SubItem subItem = new SpecParamConfigDto.SubItem();
                            String subItemName = "";
                            if(subItems.containsKey(specConfigSubItem.getSubItemId())){
                                subItemName = subItems.get(specConfigSubItem.getSubItemId()).replace("标配/选配", "").replace("&nbsp;", " ").replace("&amp;", "&");
                            }
                            subItem.setName(subItemName);
                            subItem.setValue(specConfigSubItem.getSubValue()== 1 ? "●" : "○");
                            subItem.setPriceinfo(PriceUtil.getPriceInfoNoDefult(price == null ? 0 : price.getPrice()));
                            subItem.setLogo(specConfigSubItem.getLogo());
                            subItem.setSubitemid(specConfigSubItem.getSubItemId());
                            typeItem.getSublist().add(subItem);
                        }
                    }
                    if(!typeItem.getSublist().isEmpty()){
                        typeItem.setValue("");
                    }else{
                        typeItem.setValue("-");
                    }
                }



                if (item.getDynamicShow() == 1 && currentConfigValueEqualNullNum.get() > 0){
                    continue;
                }

                specParamConfigDto.getItemMap().put(String.format("%s_%s",baseInfo.getTypeId(),item.getItemId()),typeItem);
            }
            //之家实测数据
            if(Objects.nonNull(specTestDataDto) && Objects.nonNull(configtype.get(baseInfo.getTypeName()))){
                TestStandardResult testStandardResult = specTestDataDto.getTestStandardResult();
                if (testStandardResult != null) {
                    SpecParamConfigDto.Item typeItem = new SpecParamConfigDto.Item();
                    typeItem.setValue("-");
                    typeItem.setName(configtype.get(baseInfo.getTypeName()));
                    String url = "autohome://car/ahtest?seriesid=%s&specid=%s&dataid=%s&tabid=%s&secid=%s&sourceid=";
                    if ("被动安全".equals(baseInfo.getTypeName()) && baseInfo.getItems().stream().filter(x -> x.getItemName().equals("车身稳定控制(ESC/ESP/DSC等)")).findFirst().isPresent()) {
                        TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "安全性", "麋鹿", "麋鹿成绩", "麋鹿成绩");
                        if (testDataItem != null) {
                            typeItem.setCornertype(5);
                            typeItem.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                            typeItem.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 4, testDataItem.getParentId()));
                        }
                    }else{
                        TestStandardResult.TestDataItemListDTO item1 = getDataItemByName(testStandardResult, testTab.get(baseInfo.getTypeName()), "主动安全".equals(baseInfo.getTypeName()) ? "AEB主动安全" : "");
                        if (item1 != null) {
                            typeItem.setSubvalue(configtype.get(baseInfo.getTypeName()));
                            typeItem.setCornertype(5);
                            typeItem.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), testTabId.get(baseInfo.getTypeName()), "主动安全".equals(baseInfo.getTypeName()) ? item1.getItemId() : ""));
                        }
                    }
                    if(StringUtils.isNotEmpty(typeItem.getSubvalue())){
                        specParamConfigDto.getItemMap().put(String.format("%s_%s",baseInfo.getTypeId(), TestDataConfigItemEnum.getValueByName(typeItem.getName())),typeItem);
                    }
                }
            }
        }


        return specParamConfigDto;
    }


    private TestStandardResult.TestDataItemListDTO getDataItemByName(TestStandardResult parentDataItem, String level1Name, String level2Name, String level3Name, String level4Name) {
        //性能>加速>百公里加速>0-100km/h加速时间
        if (parentDataItem == null || parentDataItem.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level1Name))
            return null;
        //性能
        TestStandardResult.TestDataItemListDTO level1ListDTO = parentDataItem.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level1Name)).findFirst().orElse(null);
        if (level1ListDTO==null|| level1ListDTO.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level2Name))
            return null;
        //加速
        TestStandardResult.TestDataItemListDTO level2ListDTO = level1ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level2Name)).findFirst().orElse(null);
        if (level2ListDTO==null|| level2ListDTO.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level3Name))
            return null;
        //百公里加速
        TestStandardResult.TestDataItemListDTO level3ListDTO = level2ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level3Name)).findFirst().orElse(null);
        if (level3ListDTO==null|| level3ListDTO.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level4Name))
            return null;
        //0-100km/h加速时间
        TestStandardResult.TestDataItemListDTO testDataItemListDTO = level3ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level4Name)).findFirst().orElse(null);
        if (testDataItemListDTO!=null) {
            //为了拿到二级tab的id
            testDataItemListDTO.setParentId(level2ListDTO.getItemId());
        }
        return testDataItemListDTO;
    }

    /**
     * 判断实测一级、二级是否有数据
     */
    private TestStandardResult.TestDataItemListDTO getDataItemByName(TestStandardResult parentDataItem, String level1Name, String level2Name) {
        //性能>加速>百公里加速>0-100km/h加速时间
        if (parentDataItem == null || parentDataItem.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level1Name))
            return null;
        //性能
        TestStandardResult.TestDataItemListDTO level1ListDTO = parentDataItem.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level1Name)).findFirst().orElse(null);
        if (level1ListDTO==null|| level1ListDTO.getTestDataItemList() == null ||level1ListDTO.getTestDataItemList().size()==0)
            return null;
        if (StringUtil.isNullOrEmpty(level2Name)) {
            return level1ListDTO;
        }
        TestStandardResult.TestDataItemListDTO level2ListDTO =  level1ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level2Name)).findFirst().orElse(null);
        if (level2ListDTO==null|| level2ListDTO.getTestDataItemList() == null || level2ListDTO.getTestDataItemList().size()==0)
            return null;
        return level2ListDTO;
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

}
