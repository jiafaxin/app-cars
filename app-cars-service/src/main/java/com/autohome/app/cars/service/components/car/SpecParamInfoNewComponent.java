package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.testdata.dtos.TestStandardResult;
import com.autohome.app.cars.common.enums.TestDataConfigItemEnum;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.common.utils.PriceUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.SpecParamInfoMapper;
import com.autohome.app.cars.mapper.popauto.entities.ParamItemPo;
import com.autohome.app.cars.mapper.popauto.entities.ParamTypePo;
import com.autohome.app.cars.mapper.popauto.entities.ParamUnionPo;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecTestDataDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SeriesParamTypeModel;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecParamConfigDto;
import com.google.common.collect.ImmutableList;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RedisConfig
@Slf4j
@DBConfig(tableName = "spec_param_info_new")
public class SpecParamInfoNewComponent extends BaseComponent<SpecParamConfigDto> {

    private static String specIdParamName = "specId";

    /**
     * 不展示电动机的燃料类型
     * 汽油	    1
     * 柴油	    2
     * 汽油+48V轻混系统	8
     * 汽油+24V轻混系统	9
     * 汽油+90V轻混系统	10
     * 天然气	11
     */
    public static final List<Integer> noElectricMotorFuelTypeList = ImmutableList.of(1, 2, 8, 9, 10, 11);
    /**
     * 纯电动	4
     * 插电式混合动力	5
     * 增程式	6
     * 氢燃料电池	7
     */
    public static final List<Integer> newEnergyFueltypeList = ImmutableList.of(4, 5, 6, 7);

    /**
     * 电动机分类：油车不显示这些分类
     */
    public static final List<String> electricTypeList = ImmutableList.of("电动机", "电池/续航", "充/放电");
    /**
     * 三电系统下的分类。全是新能源车时返回 groupname='三电系统'
     */
    public static final List<String> threeElectricType = ImmutableList.of("发动机", "电动机", "电池/续航", "充/放电", "变速箱");

    /**
     * 新能源基本参数项，只有新能源车型才显示，非新能源车型隐藏这些参数:
     * "快充电量百分比", "工信部纯电续驶里程(km)", "电池充电时间", "充电桩价格", "实测快充时间(小时)", "实测慢充时间(小时)", "快充时间(小时)", "慢充时间(小时)", "实测续航里程(km)"
     */
    public static List<String> listNewEnergyParam = ImmutableList.of("快充电量百分比", "纯电续航里程(km)", "NEDC纯电续航里程(km)", "WLTP纯电续航里程(km)", "工信部纯电续航里程(km)", "电池充电时间", "充电桩价格", "实测快充时间(小时)", "实测慢充时间(小时)", "快充时间(小时)", "慢充时间(小时)", "实测续航里程(km)");

    /**
     * 纯电动车型不显示:
     * "充电桩价格", "系统综合功率(kW)", "系统综合扭矩(N·m)"
     */
    public static List<String> listNotDisPlayOfPEVCarParam = ImmutableList.of("油箱容积(L)", "NEDC综合油耗(L/100km)", "WLTC综合油耗(L/100km)", "四驱形式", "充电桩价格", "发动机", "变速箱", "系统综合功率(kW)", "系统综合扭矩(N·m)", "工信部综合油耗(L/100km)", "实测油耗(L/100km)", "环保标准");
    private final static Map<String, TestDataConfigItemEnum> addconfigitem = new HashMap<>(); //参数项 - 实测项 对照



    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private SpecDetailComponent specDetailComponent;
    @Autowired
    private SpecTestDataComponent specTestDataComponent;
    @Autowired
    private SpecParamInfoMapper specParamInfoMapper;


    static {
        initAddConfigitem();
    }

    static void initAddConfigitem(){
        addconfigitem.put("高度(mm)", TestDataConfigItemEnum.ChengZuoKongJian);
        addconfigitem.put("四驱形式", TestDataConfigItemEnum.YueYeNengLi);
        addconfigitem.put("后制动器类型", TestDataConfigItemEnum.ShaCheJuLi);
        addconfigitem.put("WLTC综合油耗(L/100km)", TestDataConfigItemEnum.ZongHeYouHao);
        addconfigitem.put("NEDC综合油耗(L/100km)", TestDataConfigItemEnum.ZongHeYouHao);
        addconfigitem.put("CLTC纯电续航里程(km)", TestDataConfigItemEnum.ZongHeXuHang);
        addconfigitem.put("NEDC纯电续航里程(km)", TestDataConfigItemEnum.ZongHeXuHang);
        addconfigitem.put("WLTC纯电续航里程(km)", TestDataConfigItemEnum.ZongHeXuHang);
    }

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecParamConfigDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
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

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        // 所有车型
        List<SpecEntity> allSpecEntities = specMapper.getSpecAll();
        allSpecEntities.addAll(specMapper.getCvSpecAll());
        allSpecEntities = allSpecEntities.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Map<Integer, SpecEntity> specMap = allSpecEntities.stream().collect(Collectors.toMap(SpecEntity::getId, Function.identity(), (k1, k2) -> k1));
        loopSpec(totalMinutes, specId -> {
            try {
                SpecEntity spec = specMap.get(specId);
                if(Objects.nonNull(spec)){
                    getData(spec);
                }
            } catch (Exception e) {
                xxlLog.accept(specId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
        }, xxlLog);

    }

    public void refresh(int specId) {
        try {
            SpecEntity spec = specMapper.getSpec(specId);
            getData(spec);
        } catch (Exception e) {
            log.error("刷新车型配置异常-exception:{}", e);
        }
    }

    private void getData(SpecEntity spec) {
        int specId =spec.getId();
        boolean isCV = specId >= 1000000;
        List<ParamUnionPo> paramUnionPoList = specParamInfoMapper.getParamsBySpecIds(specId + "", isCV);
        Map<String, SpecParamConfigDto.Item> itemMap = getParamItemMap(specId,spec, paramUnionPoList);
        if (!itemMap.isEmpty()) {
            SpecParamConfigDto dto = new SpecParamConfigDto();
            dto.setSpecId(specId);
            dto.setItemMap(itemMap);
            update(makeParam(dto.getSpecId()), dto);
        }
    }


    private Map<String, SpecParamConfigDto.Item> getParamItemMap(Integer specId, SpecEntity spec, List<ParamUnionPo> paramsBySeriesid) {
        Map<String, SpecParamConfigDto.Item> itemMap = new LinkedHashMap<>();
        LinkedList<ParamTypePo> allParamType = getAllParamType_FromLocalCache();
        LinkedList<ParamItemPo> getAllParamItems = getAllParamItems_FromLocalCache();
        List<ParamItemPo> newAllParemItems = reBuildParamItemFrame(getAllParamItems); ////基本参数补全项处理结果

        int fuelTypeDetail = spec.getFuelType();

        boolean allSpecIsPEV = (fuelTypeDetail == 4 || fuelTypeDetail == 7);

        String WLDC_oil_key ="";//油耗标准优先级
        String NEDC_oil_key ="";
        String CLTC_elec_key ="";//续航标准优先级
        String NEDC_elec_key ="";
        String WLTC_elec_key ="";
        SpecTestDataDto specTestDataDto = specTestDataComponent.getSync(specId);
        for (ParamTypePo paramTypePo : allParamType) {


            List<ParamItemPo> paramItemPoList = newAllParemItems.stream().filter(s -> s.getTypeId().equals(paramTypePo.getParamTypeId()) && s.getIsShow().equals(1)).collect(Collectors.toList());


            for (ParamItemPo paramItemPo : paramItemPoList) {
                Integer paramId = paramItemPo.getParamId();

                //52 最大扭矩(N·m) 特殊处理
                if (paramItemPo.getTypeId().equals(1) && paramItemPo.getParamId().equals(52)) {
                    //油电混合 插电混合 基本参数下最大扭矩用系统综合扭矩（2022年新调整）
                    if (Arrays.asList(3, 5).contains(fuelTypeDetail)) {
                        paramId = 71;
                    } else if (Arrays.asList(4, 6, 7, 12).contains(fuelTypeDetail)) {
                        //基本参数下最大扭矩用 电动机总扭矩(N·m) paramId=65 （2022年新调整）
                        paramId = 65;
                    } else {
                        //52	最大扭矩(N·m)
                        paramId = 52;
                    }
                }

                //50 最大功率(kW) 特殊处理
                if (paramItemPo.getTypeId().equals(1) && paramItemPo.getParamId().equals(50)) {
                    //油电混合 插电混合 基本参数下最大功率用系统综合功率 （2022年新调整） //系统综合功率(kW) 70
                    if (Arrays.asList(3, 5).contains(fuelTypeDetail)) {
                        paramId = 70;
                    } else if (Arrays.asList(4, 6, 7, 12).contains(fuelTypeDetail)) {
                        //纯电、增程、氢、汽油电驱  基本参数下最大功率用电动机总功率(kW) paramId=63（2022年新调整）
                        paramId = 63;
                    } else {// 基本参数下最大功率用发动机最大扭矩(N·m)
                        // 基本参数下最大功率用发动机最大功率(kW)
                        //最大功率(kW) id=50
                        paramId = 50;

                    }
                }

                Integer finalParamId = paramId;
                List<ParamUnionPo> singleParamList = paramsBySeriesid.stream().filter(s -> s.getSpecid() == specId && s.getParamid().equals(finalParamId))
                        .sorted(Comparator.comparingInt(ParamUnionPo::getSubParamSort)).collect(Collectors.toList());

                SeriesParamTypeModel.ParamitemsBean.ValueitemsBean valueitem = new SeriesParamTypeModel.ParamitemsBean.ValueitemsBean(specId, "", new ArrayList<>());
                if (ListUtil.isNotEmpty(singleParamList)) {
                    //横向显示一行的参数
                    if (paramItemPo.getDisplayType() == 0) {

                        if (paramItemPo.getDataType() == 4 || paramItemPo.getDataType() == 5) {
                            //基本参数分类下的：车身结构特殊处理
                            if (paramItemPo.getTypeId().equals(1) && paramItemPo.getParamId().equals(24)) {
                                String strDoors = ""; //车门数
                                Optional<ParamUnionPo> doorOptional = paramsBySeriesid.stream().filter(a -> Integer.valueOf(a.getSpecid()).equals(specId) && a.getParamid() == 27)
                                        .sorted(Comparator.comparingInt(ParamUnionPo::getSubParamValue)).findFirst();
                                if (doorOptional.isPresent()) {
                                    strDoors = doorOptional.get().getSubParamName();
                                }
                                String strSeatCount = "";
                                Optional<ParamUnionPo> seatOptional = paramsBySeriesid.stream().filter(a -> Integer.valueOf(a.getSpecid()).equals(specId) && a.getParamid() == 28)
                                        .sorted(Comparator.comparingInt(ParamUnionPo::getSubParamValue)).findFirst();
                                if (seatOptional.isPresent()) {
                                    strSeatCount = seatOptional.get().getSubParamName();
                                }
                                String strStruct = "";
                                Optional<ParamUnionPo> structOptional = paramsBySeriesid.stream().filter(a -> Integer.valueOf(a.getSpecid()).equals(specId) && a.getParamid() == 24)
                                        .sorted(Comparator.comparingInt(ParamUnionPo::getSubParamValue)).findFirst();
                                if (structOptional.isPresent()) {
                                    strStruct = structOptional.get().getSubParamName();
                                }
                                if (specId < 1000000) {
                                    valueitem.setValue(com.autohome.app.cars.common.utils.StringUtils.format("{0}{1}{2}",
                                            StringUtils.isNotEmpty(strDoors) ? strDoors + "门" : "",
                                            StringUtils.isNotEmpty(strSeatCount) ? strSeatCount + "座" : "",
                                            StringUtils.isNotEmpty(strStruct) ? strStruct : ""));
                                } else {
                                    valueitem.setValue(com.autohome.app.cars.common.utils.StringUtils.format("{0}", StringUtils.isNotEmpty(strStruct) ? strStruct : ""));
                                }

                            } else {
                                valueitem.setValue(singleParamList.get(0).getSubParamName());
                            }

                        } else {
                            valueitem.setValue(singleParamList.get(0).getParamValue());
                        }

                    } else {
                        //标配项排第一位
                        List<ParamUnionPo> finanl_singleParamList = new ArrayList<>();
                        if (paramItemPo.getDataType() == 4 && singleParamList.size() > 1) {
                            finanl_singleParamList = singleParamList.stream().sorted(Comparator.comparingInt(ParamUnionPo::getSubParamValue)).collect(Collectors.toList());
                        } else {
                            finanl_singleParamList = singleParamList;

                        }

                        for (ParamUnionPo paramUnionPo : finanl_singleParamList) {
                            SeriesParamTypeModel.ParamitemsBean.ValueitemsBean.SublistBean sublistBean = null;
                            if (paramItemPo.getDataType() == 4) {
                                sublistBean = new SeriesParamTypeModel.ParamitemsBean.ValueitemsBean.SublistBean("", paramUnionPo.getSubParamName(), paramUnionPo.getSubParamValue(), paramUnionPo.getPrice());
                            } else if (paramItemPo.getDataType() == 5) {
                                sublistBean = new SeriesParamTypeModel.ParamitemsBean.ValueitemsBean.SublistBean(paramUnionPo.getSubParamName(), paramUnionPo.getSubParamTextValue(), paramUnionPo.getSubParamValue(), paramUnionPo.getPrice());
                            }
                            valueitem.getSublist().add(sublistBean);
                        }
                    }
                } else {
                    valueitem.setValue("-");
                }
                SpecParamConfigDto.Item item = new SpecParamConfigDto.Item();

                if (valueitem != null) {
                    String value = valueitem.getValue().replace("&nbsp;", "");
                    if ("厂商指导价(元)".equals(paramItemPo.getParamName()) && "0.00万".equals(value)) {
                        value = "暂无报价";
                    }
                    item.setValue(value);
                    item.setName(paramItemPo.getParamName());
                    if (ListUtil.isNotEmpty(valueitem.getSublist())) {
                        valueitem.getSublist().forEach(subitem -> {
                            StringBuilder sb = new StringBuilder();
                            if (com.autohome.app.cars.common.utils.StringUtils.isNotEmpty(subitem.getSubname())) {
                                sb.append(subitem.getSubname());
                            }
                            if (com.autohome.app.cars.common.utils.StringUtils.isNotEmpty(subitem.getSubvalue())) {
                                if (sb.length() > 0) {
                                    sb.append(":");
                                }
                                sb.append(subitem.getSubvalue());
                            }
                            SpecParamConfigDto.SubItem subObj = new SpecParamConfigDto.SubItem();
                            subObj.setName(sb.toString());
                            if (subitem.getPrice() > 0) {
                                subObj.setPriceinfo(PriceUtil.getPriceInfoNoDefult(subitem.getPrice()));
                            }
                            if (subitem.getOptiontype() == 1) {
                                subObj.setValue("●");
                            } else if (subitem.getOptiontype() == 2) {
                                subObj.setValue("○");
                            }
                            item.getSublist().add(subObj);
                        });
                    }
                }

                String paramKey = paramTypePo.getParamTypeId() + "_" + paramItemPo.getParamId();

                //处理 油耗、续航标准优先级标识
                if ("WLTC综合油耗(L/100km)".equals(item.getName())) {
                    WLDC_oil_key =paramKey;
                }
                if ("NEDC综合油耗(L/100km)".equals(item.getName())) {
                    NEDC_oil_key =paramKey;
                }
                if(paramTypePo.getParamTypeId()==1){
                    if ("CLTC纯电续航里程(km)".equals(item.getName())) {
                        CLTC_elec_key =paramKey;
                    }
                    if ("NEDC纯电续航里程(km)".equals(item.getName())) {
                        NEDC_elec_key =paramKey;
                    }
                    if ("WLTC纯电续航里程(km)".equals(item.getName())) {
                        WLTC_elec_key =paramKey;
                    }
                }


                //设置实测数据替换项
                setTestDataItem(item,specTestDataDto);

                itemMap.put(paramKey, item);
                //拼接参数对照 实测项数据
                appendTestDataItem(paramKey,item,paramTypePo,specTestDataDto,itemMap);
            }

        }
        //综合油耗优先级处理
        String url = "autohome://car/ahtest?seriesid=%s&specid=%s&dataid=%s&tabid=%s&secid=%s&sourceid=";
        String test_oil_key ="";
        if(StringUtils.isNotEmpty(WLDC_oil_key)){
            test_oil_key = WLDC_oil_key;
        }else if(StringUtils.isNotEmpty(NEDC_oil_key)){
            test_oil_key = NEDC_oil_key;
        }
        if(StringUtils.isNotEmpty(test_oil_key) && Objects.nonNull(specTestDataDto)){
            TestStandardResult testStandardResult = specTestDataDto.getTestStandardResult();
            TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "能耗", "油耗", "油耗", "百公里油耗");
            if (testDataItem != null) {
                SpecParamConfigDto.Item itemAdd = new SpecParamConfigDto.Item();
                itemAdd.setName(TestDataConfigItemEnum.ZongHeYouHao.getName());
                itemAdd.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                itemAdd.setCornertype(5);
                itemAdd.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 3, testDataItem.getItemId()));
                itemMap.put(test_oil_key+"_"+TestDataConfigItemEnum.ZongHeYouHao.getValue(), itemAdd);
            }

        }

        //综合续航优先级处理
        String test_elec_key ="";
        if(StringUtils.isNotEmpty(CLTC_elec_key)){
            test_elec_key = CLTC_elec_key;
        }else if(StringUtils.isNotEmpty(NEDC_elec_key)){
            test_elec_key = NEDC_elec_key;
        }else if(StringUtils.isNotEmpty(WLTC_elec_key)){
            test_elec_key = WLTC_elec_key;
        }
        if(StringUtils.isNotEmpty(test_elec_key) && Objects.nonNull(specTestDataDto)){
            TestStandardResult testStandardResult = specTestDataDto.getTestStandardResult();
            TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "能耗", "续航", "续航电耗", "综合续航里程");
            if (testDataItem != null) {
                SpecParamConfigDto.Item itemAdd = new SpecParamConfigDto.Item();
                itemAdd.setName(TestDataConfigItemEnum.ZongHeXuHang.getName());
                itemAdd.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                itemAdd.setCornertype(5);
                itemAdd.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 3, testDataItem.getItemId()));
                itemMap.put(test_elec_key+"_"+TestDataConfigItemEnum.ZongHeXuHang.getValue(), itemAdd);
            }
        }
        return itemMap;
    }


    private void setTestDataItem(SpecParamConfigDto.Item item, SpecTestDataDto specTestDataDto) {

        //之家实测数据
        if (Objects.nonNull(specTestDataDto)) {
            TestStandardResult testStandardResult = specTestDataDto.getTestStandardResult();
            if (testStandardResult != null) {
                //TODO 业务逻辑拼接 sourceid = siteid+6
                String url = "autohome://car/ahtest?seriesid=%s&specid=%s&dataid=%s&tabid=%s&secid=%s&sourceid=";
                if ("官方0-100km/h加速(s)".equals(item.getName())) {
                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "性能", "加速", "百公里加速", "0-100km/h加速时间");
                    if (testDataItem != null) {
                        item.setCornertype(5);
                        item.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                        item.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 1, testDataItem.getParentId()));
                    }
                } else if ("满载最小离地间隙(mm)".equals(item.getName()) || "空载最小离地间隙(mm)".equals(item.getName())) {
                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "性能", "越野通过性", "离地间隙", "离地间隙");
                    if (testDataItem != null) {
                        item.setCornertype(5);
                        item.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                        item.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 1, testDataItem.getParentId()));
                    }
                } else if ("快充时间(小时)".equals(item.getName())) {
                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "能耗", "充电", "充电", "30%-80%充电时长");
                    if (testDataItem != null) {
                        item.setCornertype(5);
                        item.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                        item.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 3, testDataItem.getParentId()));
                    }
                } else if ("快充功率(kW)".equals(item.getName())) {
                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "能耗", "充电", "充电", "峰值充电功率");
                    if (testDataItem != null) {
                        item.setCornertype(5);
                        item.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                        item.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 3, testDataItem.getParentId()));
                    }
                } else if ("后备厢容积(L)".equals(item.getName())) {
                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "空间", "装载空间");
                    if (testDataItem != null) {
                        item.setCornertype(5);
                        item.setSubvalue("装载空间实测");
                        item.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 2, testDataItem.getItemId()));
                    }
                }
            }
        }
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
        if (level1ListDTO == null || level1ListDTO.getTestDataItemList() == null || level1ListDTO.getTestDataItemList().size() == 0)
            return null;
        if (StringUtil.isNullOrEmpty(level2Name)) {
            return level1ListDTO;
        }
        TestStandardResult.TestDataItemListDTO level2ListDTO = level1ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level2Name)).findFirst().orElse(null);
        if (level2ListDTO == null || level2ListDTO.getTestDataItemList() == null || level2ListDTO.getTestDataItemList().size() == 0)
            return null;
        return level2ListDTO;
    }

    private void appendTestDataItem(String paramKey, SpecParamConfigDto.Item item, ParamTypePo paramTypePo, SpecTestDataDto specTestDataDto, Map<String, SpecParamConfigDto.Item> itemMap) {
        //参数项匹配到，后面拼接实测项
        if(addconfigitem.containsKey(item.getName()) && Objects.nonNull(specTestDataDto)){
            //TODO 业务逻辑拼接 sourceid = siteid+6
            String url = "autohome://car/ahtest?seriesid=%s&specid=%s&dataid=%s&tabid=%s&secid=%s&sourceid=";
            TestStandardResult testStandardResult = specTestDataDto.getTestStandardResult();
            TestDataConfigItemEnum testEnum = addconfigitem.get(item.getName());
            if(testStandardResult != null){
                SpecParamConfigDto.Item itemAdd = new SpecParamConfigDto.Item();
                itemAdd.setName(testEnum.getName());
                if ("高度(mm)".equals(item.getName())) {
                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "空间", "乘坐空间");
                    if (testDataItem != null) {
                        itemAdd.setCornertype(5);
                        itemAdd.setSubvalue(testEnum.getName());
                        itemAdd.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 2, testDataItem.getItemId()));
                    }
                } else if ("四驱形式".equals(item.getName())) {
                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "性能", "越野通过性");
                    if (testDataItem != null) {
                        itemAdd.setCornertype(5);
                        itemAdd.setSubvalue(testEnum.getName());
                        itemAdd.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 1, testDataItem.getItemId()));
                    }
                } else if ("后制动器类型".equals(item.getName())) {
                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "安全性", "刹车", "刹车距离", "刹车距离");
                    if (testDataItem != null) {
                        itemAdd.setCornertype(5);
                        itemAdd.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                        itemAdd.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 4, testDataItem.getItemId()));
                    }
                }
                if(StringUtils.isNotEmpty(itemAdd.getSubvalue())){
                    itemMap.put(paramKey+ "_"+testEnum.getValue(), itemAdd);
                }
            }


        }
    }

    private TestStandardResult.TestDataItemListDTO getDataItemByName(TestStandardResult parentDataItem, String level1Name, String level2Name, String level3Name, String level4Name) {
        //性能>加速>百公里加速>0-100km/h加速时间
        if (parentDataItem == null || parentDataItem.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level1Name))
            return null;
        //性能
        TestStandardResult.TestDataItemListDTO level1ListDTO = parentDataItem.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level1Name)).findFirst().orElse(null);
        if (level1ListDTO == null || level1ListDTO.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level2Name))
            return null;
        //加速
        TestStandardResult.TestDataItemListDTO level2ListDTO = level1ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level2Name)).findFirst().orElse(null);
        if (level2ListDTO == null || level2ListDTO.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level3Name))
            return null;
        //百公里加速
        TestStandardResult.TestDataItemListDTO level3ListDTO = level2ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level3Name)).findFirst().orElse(null);
        if (level3ListDTO == null || level3ListDTO.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level4Name))
            return null;
        //0-100km/h加速时间
        TestStandardResult.TestDataItemListDTO testDataItemListDTO = level3ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level4Name)).findFirst().orElse(null);
        if (testDataItemListDTO != null) {
            //为了拿到二级tab的id
            testDataItemListDTO.setParentId(level2ListDTO.getItemId());
        }
        return testDataItemListDTO;
    }

    /**
     * 参数分类 localcache
     *
     * @return
     */
    private LinkedList<ParamTypePo> getAllParamType_FromLocalCache() {
        LinkedList<ParamTypePo> paramTypePos = specParamInfoMapper.getAllParamType();
        return paramTypePos;
    }

    /**
     * 参数项缓存 localcache
     *
     * @return
     */
    private LinkedList<ParamItemPo> getAllParamItems_FromLocalCache() {
        LinkedList<ParamItemPo> paramItems = specParamInfoMapper.getAllParamItems();
        return paramItems;
    }

    /**
     * 重构参数基本项，自动补全一些基本参数分类下的参数项
     * 以下参数在基本参数里需要自动补位项
     * 24	车身结构	车身
     * 50	最大功率(kW)	发动机
     * 52	最大扭矩(N·m)	发动机
     * 55	能源类型	发动机
     * 61	环保标准	发动机
     * 63	电动机总功率(kW)	电动机
     * 65	电动机总扭矩(N·m)	电动机
     * 70	系统综合功率(kW)	电动机
     * 71	系统综合扭矩(N·m)	电动机
     * 75	NEDC纯电续航里程(km)	电动机
     * 101	CLTC纯电续航里程(km)	电动机
     * 103	EPA纯电续航里程(km)	电动机
     * 108	简称	变速箱
     * 135	WLTC纯电续航里程(km)	电动机
     * 79	电池快充时间
     * 81	电池慢充时间
     *
     * @param getAllParamItems
     * @return list
     */
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

}
