package com.autohome.app.cars.service.components.car;

import autohome.rpc.car.app_cars.v1.carcfg.Item;
import com.autohome.app.cars.apiclient.baike.dtos.ConfigBaikeLinkDto;
import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.enums.TestDataConfigItemEnum;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.mapper.popauto.ConfigMapper;
import com.autohome.app.cars.mapper.popauto.SpecParamInfoMapper;
import com.autohome.app.cars.mapper.popauto.entities.ConfigItemEntity;
import com.autohome.app.cars.mapper.popauto.entities.ParamItemPo;
import com.autohome.app.cars.mapper.popauto.entities.ParamTypePo;
import com.autohome.app.cars.mapper.popauto.entities.SpecParamEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.baike.BaikelinkforconfigComponent;
import com.autohome.app.cars.service.components.car.dtos.SpecParamConfigTempDto;
import com.autohome.app.cars.service.services.dtos.ParamConfigVideoInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SpecParamConfigTempComponent extends BaseComponent<SpecParamConfigTempDto> {

    @Autowired
    private SpecParamInfoMapper specParamInfoMapper;
    @Autowired
    ConfigMapper configMapper;

    @Autowired
    private BaikelinkforconfigComponent baikelinkforconfigComponent;

    private final static TreeMap<String, ParamConfigVideoInfo> parmconfigvideoinfo = new TreeMap<>();
    private final static Map<String, TestDataConfigItemEnum> addparamitem = new HashMap<>(); //参数项 - 实测项 对照
    private final static Map<String, TestDataConfigItemEnum> addconfigitem = new HashMap<>(); //配置项 - 实测项 对照

    static {
        initParmconfigvideoinfo();
        initAddParamitem();
        initAddConfigitem();
    }
    public SpecParamConfigTempDto get() {
        return baseGet(null);
    }
    public CompletableFuture<SpecParamConfigTempDto> getAsync() {
        return baseGetAsync(null);
    }

    public void refreshAll(){
        Map<String, ConfigBaikeLinkDto> baikeMap = baikelinkforconfigComponent.getMap().join();
        LinkedList<ParamTypePo> paramTypePos = specParamInfoMapper.getAllParamType();
        LinkedList<ParamItemPo> paramItems = specParamInfoMapper.getAllParamItems();
        List<ParamItemPo> paramItemList = reBuildParamItemFrame(paramItems);
        SpecParamConfigTempDto result = new SpecParamConfigTempDto();
        //参数项模板
        SpecParamConfigTempDto.ParamTemp paramTemp = new SpecParamConfigTempDto.ParamTemp();
        //参数模版项map id - 名称
        Map<String,String > paramItemMap =new HashMap<>();
        Map<Integer, SpecParamConfigTempDto.ParamTemp.ParamType> types = new HashMap<>();
        for (ParamTypePo paramType : paramTypePos) {
            SpecParamConfigTempDto.ParamTemp.ParamType type = new SpecParamConfigTempDto.ParamTemp.ParamType();
            type.setId(paramType.getParamTypeId());
            type.setName(paramType.getParamTypeName());
            paramTemp.getParamTypes().add(type);
            types.put(type.getId(), type);
        }

        for (ParamItemPo paramItem : paramItemList) {
            if(!types.containsKey(paramItem.getTypeId())){
                continue;
            }
            ConfigBaikeLinkDto baike = baikeMap.containsKey(paramItem.getParamName())?baikeMap.get(paramItem.getParamName()):null;
            SpecParamConfigTempDto.ParamTemp.ParamItem item = new SpecParamConfigTempDto.ParamTemp.ParamItem();
            item.setDisplayType(paramItem.getDisplayType());
            item.setDataType(paramItem.getDataType());
            item.setDynamicShow(paramItem.getDynamicShow());
            item.setId(paramItem.getParamId());
            item.setName(paramItem.getParamName());
            if(baike!=null){
                item.setBaikeId(baike.getId());
            }
            ParamConfigVideoInfo videoInfo = parmconfigvideoinfo.get(paramItem.getParamName());
            if(videoInfo!=null){
                item.setContentId(videoInfo.getContentid());
                item.setPlayStartTime(videoInfo.getPlaystarttime());
            }

            SpecParamConfigTempDto.ParamTemp.ParamType type = types.get(paramItem.getTypeId());
            type.getParamItems().add(item);
            //拼接实测、参考价、优惠信息 模板项
            paramItemMap.put(item.getName(),"p-"+type.getId()+"-"+item.getId());
            appendParamItem(item,type);
        }
        paramTemp.setItemMap(paramItemMap);
        result.setParam(paramTemp);
        //配置项模板
        SpecParamConfigTempDto.ConfigTemp configTemp = new SpecParamConfigTempDto.ConfigTemp();
        List<ConfigItemEntity> configItems = configMapper.getAllConfig();
        Map<Integer, List<ConfigItemEntity>> configTypeMap = configItems.stream().collect(Collectors.groupingBy(config ->
                config.getTypeId(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
        //配置模版项map id - 名称
        Map<String,String > configItemMap =new HashMap<>();
        for (Integer typeId:configTypeMap.keySet()){
            List<ConfigItemEntity> configItemEntities = configTypeMap.get(typeId);
            if(Objects.isNull(configItemEntities) || configItemEntities.isEmpty()){
                continue;
            }
            ConfigItemEntity firstItem = configItemEntities.get(0);
            SpecParamConfigTempDto.ConfigTemp.ConfigType configType =new SpecParamConfigTempDto.ConfigTemp.ConfigType();
            configType.setId(typeId);
            configType.setName(firstItem.getTypeName());
            configType.setGroupname( Spec.DicConfig_Group.containsKey(firstItem.getTypeName()) ? Spec.DicConfig_Group.get(firstItem.getTypeName()) : "");

            configItemEntities.forEach(item->{
                if(item.getIsShow()!=1){
                    return;
                }
                SpecParamConfigTempDto.ConfigTemp.ConfigItem configItem =new SpecParamConfigTempDto.ConfigTemp.ConfigItem();
                configItem.setDataType(0);
                configItem.setDisplayType(item.getDisplayType());
                configItem.setDynamicShow(item.getDynamicShow());
                configItem.setCVIsShow(item.getCVIsShow());
                configItem.setIsShow(item.getIsShow());
                String itemname = item.getItemName().replace("&nbsp;", " ").replace("&amp;", "&");
                ConfigBaikeLinkDto baike = baikeMap.containsKey(itemname)?baikeMap.get(itemname):null;
                if(baike!=null){
                    configItem.setBaikeId(baike.getId());
                }
                ParamConfigVideoInfo videoInfo = parmconfigvideoinfo.get(item.getItemName());
                if(videoInfo!=null){
                    configItem.setContentId(videoInfo.getContentid());
                    configItem.setPlayStartTime(videoInfo.getPlaystarttime());
                }
                configItem.setId(item.getItemId());
                configItem.setName(itemname);
                configItemMap.put(itemname,"c-"+configType.getId()+"-"+configItem.getId());
                configType.getParamItems().add(configItem);
            });
            appendConfigItem(configType);
            configTemp.getParamTypes().add(configType);
        }
        configTemp.setItemMap(configItemMap);
        result.setConfig(configTemp);
        try {
            update(null,result);
        } catch (Exception e) {
            log.error("参配项模板 error",e);
        }
    }

    private void appendParamItem(SpecParamConfigTempDto.ParamTemp.ParamItem item, SpecParamConfigTempDto.ParamTemp.ParamType type) {
        //参数项匹配到，后面拼接实测项
        if(addparamitem.containsKey(item.getName())){
            TestDataConfigItemEnum testEnum = addparamitem.get(item.getName());
            SpecParamConfigTempDto.ParamTemp.ParamItem itemAdd = new SpecParamConfigTempDto.ParamTemp.ParamItem();
            itemAdd.setDisplayType(1);
            itemAdd.setDataType(1000);
            itemAdd.setDynamicShow(1);
            itemAdd.setContentId(testEnum.getValue()+"");
            itemAdd.setId(item.getId());
            itemAdd.setName(testEnum.getName());
            type.getParamItems().add(itemAdd);
        } else if("车型名称".equals(item.getName())){
            //基本参数-参考价(元)
            SpecParamConfigTempDto.ParamTemp.ParamItem priceItem = new SpecParamConfigTempDto.ParamTemp.ParamItem();
            priceItem.setDisplayType(1);
            priceItem.setDataType(0);
            priceItem.setDynamicShow(1);
            priceItem.setContentId("");
            priceItem.setId(100003);
            priceItem.setName("参考价(元)");
            type.getParamItems().add(priceItem);
            //基本参数-优惠信息
            SpecParamConfigTempDto.ParamTemp.ParamItem disItem = new SpecParamConfigTempDto.ParamTemp.ParamItem();
            disItem.setDisplayType(1);
            disItem.setDataType(0);
            disItem.setDynamicShow(1);
            disItem.setContentId("");
            disItem.setId(100005);
            disItem.setName("优惠信息");
            type.getParamItems().add(disItem);
        } else if("厂商指导价(元)".equals(item.getName())){
            //基本参数-优惠信息
            SpecParamConfigTempDto.ParamTemp.ParamItem disItem = new SpecParamConfigTempDto.ParamTemp.ParamItem();
            disItem.setDisplayType(1);
            disItem.setDataType(0);
            disItem.setDynamicShow(1);
            disItem.setContentId("");
            disItem.setId(100006);
            disItem.setName("优惠信息");
            type.getParamItems().add(disItem);
        }

    }

    private void appendConfigItem(SpecParamConfigTempDto.ConfigTemp.ConfigType type) {
        //参数项匹配到，后面拼接实测项
        if(addconfigitem.containsKey(type.getName())){
            TestDataConfigItemEnum testEnum = addconfigitem.get(type.getName());
            SpecParamConfigTempDto.ConfigTemp.ConfigItem itemAdd = new SpecParamConfigTempDto.ConfigTemp.ConfigItem();
            itemAdd.setDisplayType(1);
            itemAdd.setDataType(1000);
            itemAdd.setDynamicShow(1);
            itemAdd.setContentId(testEnum.getValue()+"");
            itemAdd.setId(type.getId());
            itemAdd.setName(testEnum.getName());
            type.getParamItems().add(itemAdd);
        }

    }


    private List<ParamItemPo> reBuildParamItemFrame(LinkedList<ParamItemPo> getAllParamItems) {
        //基本参数补全项处理
        List<ParamItemPo> newAllParemItems = new ArrayList<>();
        List<Integer> listA = Arrays.asList(55, 61); //能源类型、环保标准
        List<Integer> listB = Arrays.asList(75, 135, 101, 103, 79, 81, 80, 82, 50, 52, 108, 24);//顺序别动了。

        List<ParamItemPo> listCopy = new ArrayList<>(); //临时变量copy一份所有参数项。
        for (ParamItemPo po : getAllParamItems) {
            ParamItemPo temp = new ParamItemPo();
            BeanUtils.copyProperties(po, temp);
            listCopy.add(temp);
        }

        for (ParamItemPo po : getAllParamItems) {
            if(po.getIsShow()!=1){
                continue;
            }
            if (!newAllParemItems.contains(po)) {
                newAllParemItems.add(po);
            }
            if (po.getParamName().equals("级别")) {
                listA.forEach(p -> {
                    Optional<ParamItemPo> optional = listCopy.stream().filter(a -> a.getParamId().equals(p)).findFirst();
                    if (optional.isPresent()) {
                        ParamItemPo tempPo2 = optional.get();
                        tempPo2.setTypeId(1);
                        newAllParemItems.add(tempPo2);
                    }
                });

            } else if (po.getParamName().equals("上市时间")) {
                listB.forEach(p -> {
                    Optional<ParamItemPo> optional = listCopy.stream().filter(a -> a.getParamId().equals(p)).findFirst();
                    if (optional.isPresent()) {
                        ParamItemPo tempPo = optional.get();
                        tempPo.setTypeId(1);
                        if (tempPo.getParamId().equals(108)) {
                            tempPo.setParamName("变速箱");
                        }
                        newAllParemItems.add(tempPo);
                    }
                });
            }
        }
        return newAllParemItems;
    }

    static void initParmconfigvideoinfo() {
        parmconfigvideoinfo.put("燃油标号", new ParamConfigVideoInfo("燃油标号", "34FEF20296E731A8", 6));
        parmconfigvideoinfo.put("供油方式", new ParamConfigVideoInfo("供油方式", "7819EA4FDCA83D8B", 6));
        parmconfigvideoinfo.put("驻车制动类型", new ParamConfigVideoInfo("驻车制动类型", "2210205ED65207ED", 6));
        parmconfigvideoinfo.put("进气形式", new ParamConfigVideoInfo("进气方式", "EF127A4540BA721C", 6));
        // parmconfigvideoinfo.put("四驱形式",new
        // ParmConfigVideoInfo("四驱形式","F10767BC0B2F7B5A",6));
        parmconfigvideoinfo.put("最大马力(Ps)", new ParamConfigVideoInfo("最大马力", "6BC427D9E070FFC2", 16));
        parmconfigvideoinfo.put("最大功率(kW)", new ParamConfigVideoInfo("最大功率", "6BC427D9E070FFC2", 16));
        parmconfigvideoinfo.put("最大扭矩(N·m)", new ParamConfigVideoInfo("最大扭矩", "6BC427D9E070FFC2", 47));
        parmconfigvideoinfo.put("驱动方式", new ParamConfigVideoInfo("驱动方式", "7D7E01E37C754293", 5));
        parmconfigvideoinfo.put("车体结构", new ParamConfigVideoInfo("车体结构", "BDF9D05B39C45BD9", 27));
        parmconfigvideoinfo.put("变速箱类型", new ParamConfigVideoInfo("变速箱类型", "DFD7C9AFFADC78D4", 11));
        parmconfigvideoinfo.put("燃料形式", new ParamConfigVideoInfo("燃料形式", "A020354BBF50A800", 12));
        parmconfigvideoinfo.put("排量(mL)", new ParamConfigVideoInfo("排量", "A020354BBF50A800", 34));
        parmconfigvideoinfo.put("气缸数(个)", new ParamConfigVideoInfo("气缸数", "A020354BBF50A800", 53));
        parmconfigvideoinfo.put("气缸排列形式", new ParamConfigVideoInfo("气缸排列形式", "A020354BBF50A800", 44));
        parmconfigvideoinfo.put("每缸气门数(个)", new ParamConfigVideoInfo("每缸气门数", "A020354BBF50A800", 65));
        parmconfigvideoinfo.put("压缩比", new ParamConfigVideoInfo("压缩比", "A020354BBF50A800", 75));
        parmconfigvideoinfo.put("前轮胎规格", new ParamConfigVideoInfo("前轮轮胎规格", "50E34F56BB808FB6", 32));
        parmconfigvideoinfo.put("后轮胎规格", new ParamConfigVideoInfo("后轮轮胎规格", "50E34F56BB808FB6", 32));
        parmconfigvideoinfo.put("备胎规格", new ParamConfigVideoInfo("备胎规格", "50E34F56BB808FB6", 20));
        parmconfigvideoinfo.put("前悬架类型", new ParamConfigVideoInfo("前悬架类型", "EB7DB50FBC223702", 6));
        parmconfigvideoinfo.put("后悬架类型", new ParamConfigVideoInfo("后悬架类型", "EB7DB50FBC223702", 6));
        parmconfigvideoinfo.put("助力类型", new ParamConfigVideoInfo("助力类型", "0B6ADAB9CC0161D1", 18));
        parmconfigvideoinfo.put("前制动器类型", new ParamConfigVideoInfo("前制动器类型", "57C703C787B9867B", 13));
        parmconfigvideoinfo.put("后制动器类型", new ParamConfigVideoInfo("后制动器类型", "57C703C787B9867B", 13));
        parmconfigvideoinfo.put("巡航系统", new ParamConfigVideoInfo("巡航系统", "32C74D2B0185D540", 11));
        // parmconfigvideoinfo.put("自适应巡航",new
        // ParmConfigVideoInfo("自适应巡航","32C74D2B0185D540",40));
        // parmconfigvideoinfo.put("全速自适应巡航",new
        // ParmConfigVideoInfo("全速自适应巡航","32C74D2B0185D540",93));
        parmconfigvideoinfo.put("车道保持辅助系统", new ParamConfigVideoInfo("车道保持辅助系统", "B3E3A84B8956DA76", 75));
        parmconfigvideoinfo.put("近光灯光源", new ParamConfigVideoInfo("近灯光光源", "B0BED8F0CCEDA863", 11));
        parmconfigvideoinfo.put("远光灯光源", new ParamConfigVideoInfo("远光灯光源", "B0BED8F0CCEDA863", 11));
        parmconfigvideoinfo.put("手机互联/映射", new ParamConfigVideoInfo("手机互联/映射", "DC41524164C92533", 19));
        parmconfigvideoinfo.put("限滑差速器/差速锁", new ParamConfigVideoInfo("限滑差速器/差速锁", "B2462C5897F9132E", 12));
        parmconfigvideoinfo.put("可变悬架功能", new ParamConfigVideoInfo("可变悬架功能", "38A22C57DB5007E2", 15));
        parmconfigvideoinfo.put("空气悬架", new ParamConfigVideoInfo("空气悬架", "38A22C57DB5007E2", 28));
        parmconfigvideoinfo.put("电磁感应悬架", new ParamConfigVideoInfo("电磁感应悬架", "38A22C57DB5007E2", 44));
        parmconfigvideoinfo.put("主动降噪", new ParamConfigVideoInfo("主动降噪", "5B7F3E721F99A606", 15));
        parmconfigvideoinfo.put("远程启动功能", new ParamConfigVideoInfo("远程启动", "AE2C70F660993824", 16));
        parmconfigvideoinfo.put("自适应远近光", new ParamConfigVideoInfo("自适应远近光", "D3E77300E2A0C8F5", 80));
        parmconfigvideoinfo.put("感应后备厢", new ParamConfigVideoInfo("感应后备厢", "81DC2B052F755FDA", 63));
        parmconfigvideoinfo.put("整体主动转向系统", new ParamConfigVideoInfo("整体主动转向系统", "6357314F12A13D8F", 18));
        parmconfigvideoinfo.put("主动刹车/主动安全系统", new ParamConfigVideoInfo("主动刹车/主动安全系统", "8A67E81D4B4F936D", 25));
        parmconfigvideoinfo.put("车道偏离预警系统", new ParamConfigVideoInfo("车道偏离预警", "B3E3A84B8956DA76", 37));
        parmconfigvideoinfo.put("并线辅助", new ParamConfigVideoInfo("并线辅助", "B3E3A84B8956DA76", 11));
        parmconfigvideoinfo.put("发动机启停技术", new ParamConfigVideoInfo("发动机启停技术", "EB87110D10A9DFBE", 15));
        parmconfigvideoinfo.put("自动泊车入位", new ParamConfigVideoInfo("自动泊车入位", "C27B996E6036F731", 11));
        parmconfigvideoinfo.put("转向头灯", new ParamConfigVideoInfo("转向头灯", "D3E77300E2A0C8F5", 45));
        parmconfigvideoinfo.put("转向辅助灯", new ParamConfigVideoInfo("转向辅助灯", "D3E77300E2A0C8F5", 17));
        parmconfigvideoinfo.put("HUD抬头数字显示", new ParamConfigVideoInfo("HUD抬头数字显示", "EA4291DD6E8032E1", 23));
        parmconfigvideoinfo.put("电动后备厢", new ParamConfigVideoInfo("电动后备厢", "81DC2B052F755FDA", 14));
        parmconfigvideoinfo.put("前排座椅功能", new ParamConfigVideoInfo("前排座椅功能", "38BBD9C55C666A2E", 6));
        parmconfigvideoinfo.put("陡坡缓降", new ParamConfigVideoInfo("陡坡缓降", "93AE11ADAE5241C4", 53));
        parmconfigvideoinfo.put("自动驻车", new ParamConfigVideoInfo("自动驻车", "2210205ED65207ED", 51));
        parmconfigvideoinfo.put("上坡辅助", new ParamConfigVideoInfo("上坡辅助", "93AE11ADAE5241C4", 15));
        parmconfigvideoinfo.put("无钥匙进入功能", new ParamConfigVideoInfo("无钥匙进入系统", "5BCC963988E3F107", 15));
        parmconfigvideoinfo.put("无钥匙启动系统", new ParamConfigVideoInfo("无钥匙启动系统", "5BCC963988E3F107", 44));
        parmconfigvideoinfo.put("ISOFIX儿童座椅接口", new ParamConfigVideoInfo("ISOFIX儿童座椅接口", "E7F0D1D72E39ACB9", 13));
        parmconfigvideoinfo.put("零胎压继续行驶", new ParamConfigVideoInfo("零胎压继续行驶", "7319A7BD46E02426", 77));
        parmconfigvideoinfo.put("胎压监测功能", new ParamConfigVideoInfo("胎压监测装置", "7319A7BD46E02426", 23));
        // parmconfigvideoinfo.put("流媒体后视镜",new
        // ParmConfigVideoInfo("流媒体后视镜","65C67DBE7BA427E2",6));
        parmconfigvideoinfo.put("内后视镜功能", new ParamConfigVideoInfo("内后视镜功能", "8BAD56D571FE1A6A", 6));
        parmconfigvideoinfo.put("主/副驾驶座安全气囊", new ParamConfigVideoInfo("主/副驾驶座安全气囊", "3143F1B75C62BEBE", 35));
        parmconfigvideoinfo.put("前/后排侧气囊", new ParamConfigVideoInfo("前/后排安全气囊", "3143F1B75C62BEBE", 48));
        parmconfigvideoinfo.put("前/后排头部气囊(气帘)", new ParamConfigVideoInfo("前/后前/后排头部气囊(气帘)", "3143F1B75C62BEBE", 55));
        parmconfigvideoinfo.put("膝部气囊", new ParamConfigVideoInfo("膝部气囊", "3143F1B75C62BEBE", 70));
        // parmconfigvideoinfo.put("后排中央安全气囊",new
        // ParmConfigVideoInfo("后排中央安全气囊","3143F1B75C62BEBE",109));
        parmconfigvideoinfo.put("ABS防抱死", new ParamConfigVideoInfo("ABS防抱死", "E252C132D63F3C1E", 22));
        parmconfigvideoinfo.put("制动力分配(EBD/CBC等)", new ParamConfigVideoInfo("制动力分配(EBD/CBC等)", "E252C132D63F3C1E", 41));
        parmconfigvideoinfo.put("刹车辅助(EBA/BAS/BA等)",
                new ParamConfigVideoInfo("刹车辅助(EBA/BAS/BA等)", "E252C132D63F3C1E", 67));
        parmconfigvideoinfo.put("牵引力控制(ASR/TCS/TRC等)",
                new ParamConfigVideoInfo("牵引力控制(ASR/TCS/TRC等)", "E252C132D63F3C1E", 84));
        parmconfigvideoinfo.put("车身稳定控制(ESC/ESP/DSC等)",
                new ParamConfigVideoInfo("车身稳定控制(ESC/ESP/DSC等)", "E252C132D63F3C1E", 112));
        parmconfigvideoinfo.put("涉水感应系统", new ParamConfigVideoInfo("涉水感应系统", "0243E0ED5D06B36B", 10));
        parmconfigvideoinfo.put("全液晶仪表盘", new ParamConfigVideoInfo("全液晶仪表盘", "2CF0B114770326F4", 13));
        parmconfigvideoinfo.put("道路交通标识识别", new ParamConfigVideoInfo("道路交通标示识别", "F0DFC82C3C1E1E40", 19));
        parmconfigvideoinfo.put("天窗类型", new ParamConfigVideoInfo("天窗类型", "72CEB7518B3F5039", 6));
        parmconfigvideoinfo.put("感应雨刷功能", new ParamConfigVideoInfo("感应雨刷功能", "D4B3382669148133", 6));
        parmconfigvideoinfo.put("主座椅调节方式", new ParamConfigVideoInfo("主座椅调节方式", "115BB005DB3BA48A", 6));
        parmconfigvideoinfo.put("副座椅调节方式", new ParamConfigVideoInfo("副座椅调节方式", "115BB005DB3BA48A", 6));
        parmconfigvideoinfo.put("电动吸合车门", new ParamConfigVideoInfo("电动吸合车门", "5D89483FF92603FF", 19));
        parmconfigvideoinfo.put("被动行人保护", new ParamConfigVideoInfo("行人保护", "8F72525A98F7A6D3", 11));
        parmconfigvideoinfo.put("自动驾驶技术", new ParamConfigVideoInfo("自动驾驶技术", "14CEC66C60037AC8", 6));
        parmconfigvideoinfo.put("多层隔音玻璃", new ParamConfigVideoInfo("多层隔音玻璃", "DC5606D5B35027CE", 12));
        parmconfigvideoinfo.put("侧滑门形式", new ParamConfigVideoInfo("侧滑门行驶", "906A8B0FE20F85C6", 6));
    }
    static void initAddParamitem(){
        addparamitem.put("高度(mm)", TestDataConfigItemEnum.ChengZuoKongJian);
        addparamitem.put("四驱形式", TestDataConfigItemEnum.YueYeNengLi);
        addparamitem.put("后制动器类型", TestDataConfigItemEnum.ShaCheJuLi);
        addparamitem.put("WLTC综合油耗(L/100km)", TestDataConfigItemEnum.ZongHeYouHao);
        addparamitem.put("NEDC综合油耗(L/100km)", TestDataConfigItemEnum.ZongHeYouHao);
        addparamitem.put("CLTC纯电续航里程(km)", TestDataConfigItemEnum.ZongHeXuHang);
        addparamitem.put("NEDC纯电续航里程(km)", TestDataConfigItemEnum.ZongHeXuHang);
        addparamitem.put("WLTC纯电续航里程(km)", TestDataConfigItemEnum.ZongHeXuHang);
    }

    static void initAddConfigitem(){
        addconfigitem.put("主动安全", TestDataConfigItemEnum.ZhuDongAnQuan);
        addconfigitem.put("驾驶功能", TestDataConfigItemEnum.ZhiNengJiaShi);
        addconfigitem.put("智能化配置", TestDataConfigItemEnum.ZhiNengZuoCang);
    }

}
