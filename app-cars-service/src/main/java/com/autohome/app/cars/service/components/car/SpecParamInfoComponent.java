package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.SpecParamInfoMapper;
import com.autohome.app.cars.mapper.popauto.entities.ParamItemPo;
import com.autohome.app.cars.mapper.popauto.entities.ParamTypePo;
import com.autohome.app.cars.mapper.popauto.entities.ParamUnionPo;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SeriesParamTypeModel;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecParamInfoDto;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RedisConfig
@Slf4j
@DBConfig(tableName = "spec_param_info")
public class SpecParamInfoComponent extends BaseComponent<SpecParamInfoDto> {

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

    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private SpecDetailComponent specDetailComponent;
    @Autowired
    private SpecParamInfoMapper specParamInfoMapper;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecParamInfoDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecParamInfoDto>> get(List<Integer> specIdList) {
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
            log.error("刷新车型配置异常-exception:{}", e);
        }
    }

    private void getData(int specId){
        boolean isCV = specId >= 1000000;
        List<ParamUnionPo> paramUnionPoList = specParamInfoMapper.getParamsBySpecIds(String.valueOf(specId), isCV);
        List<SeriesParamTypeModel> seriesParamTypeModels = settleSeriesParamTypeModels(Arrays.asList(specId), paramUnionPoList);
        if (ListUtil.isNotEmpty(seriesParamTypeModels)) {
            SpecParamInfoDto dto = new SpecParamInfoDto();
            dto.setSpecId(specId);
            dto.setRsptmList(seriesParamTypeModels);
            update(makeParam(dto.getSpecId()), dto);
        }
    }

    @Override
    protected SpecParamInfoDto sourceData(TreeMap<String, Object> params) {
        SpecParamInfoDto dto = null;
        try {
            int specId = (Integer) params.get(specIdParamName);
            boolean isCV = specId >= 1000000;
            List<ParamUnionPo> paramUnionPoList = specParamInfoMapper.getParamsBySpecIds(String.valueOf(specId), isCV);
            //dto = getSpecParamBySpecid(specId, paramsBySpecIds);
            List<SeriesParamTypeModel> seriesParamTypeModels = settleSeriesParamTypeModels(Arrays.asList(specId), paramUnionPoList);
            if (ListUtil.isNotEmpty(seriesParamTypeModels)) {
                dto = new SpecParamInfoDto();
                dto.setSpecId(specId);
                dto.setRsptmList(seriesParamTypeModels);
                update(makeParam(dto.getSpecId()), dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    @Override
    protected Map<TreeMap<String, Object>, SpecParamInfoDto> sourceDatas(List<TreeMap<String, Object>> params) {
        Map<TreeMap<String, Object>, SpecParamInfoDto> result = new LinkedHashMap<>();
        try {
            List<Integer> specIdList = params.stream().map(map -> (Integer) map.get(specIdParamName)).collect(Collectors.toList());

            List<Integer> pvSpecIdList = specIdList.stream().filter(s -> s < 100 * 10000).collect(Collectors.toList());
            List<Integer> cvSpecIdList = specIdList.stream().filter(s -> s >= 100 * 10000).collect(Collectors.toList());

            List<ParamUnionPo> paramsBySpecIds = new ArrayList<>();

            if (ListUtil.isNotEmpty(pvSpecIdList)) {
                List<ParamUnionPo> pvParamsBySpecIds = specParamInfoMapper.getParamsBySpecIds(StringUtils.join(pvSpecIdList, ","), false);
                if (ListUtil.isNotEmpty(pvParamsBySpecIds)) {
                    paramsBySpecIds.addAll(pvParamsBySpecIds);
                }
            }
            if (ListUtil.isNotEmpty(cvSpecIdList)) {
                List<ParamUnionPo> cvParamsBySpecIds = specParamInfoMapper.getParamsBySpecIds(StringUtils.join(cvSpecIdList, ","), true);
                if (ListUtil.isNotEmpty(cvParamsBySpecIds)) {
                    paramsBySpecIds.addAll(cvParamsBySpecIds);
                }
            }

            specIdList.forEach(specId -> {
                List<ParamUnionPo> paramUnionPoList = paramsBySpecIds.stream().filter(x -> x.getSpecid() == specId).collect(Collectors.toList());
                //SpecParamInfoDto dto = getSpecParamBySpecid(specId, abc);
                List<SeriesParamTypeModel> seriesParamTypeModels = settleSeriesParamTypeModels(Arrays.asList(specId), paramUnionPoList);
                if (ListUtil.isNotEmpty(seriesParamTypeModels)) {
                    SpecParamInfoDto dto = new SpecParamInfoDto();
                    dto.setSpecId(specId);
                    dto.setRsptmList(seriesParamTypeModels);
                    result.put(makeParam(specId), dto);
                    update(makeParam(specId), dto);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 处理车型参数
     *
     * @param specIdList
     * @param paramsBySeriesid
     * @return list
     */
    private List<SeriesParamTypeModel> settleSeriesParamTypeModels(List<Integer> specIdList, List<ParamUnionPo> paramsBySeriesid) {
        LinkedList<ParamTypePo> allParamType = getAllParamType_FromLocalCache();
        LinkedList<ParamItemPo> getAllParamItems = getAllParamItems_FromLocalCache();
        List<ParamItemPo> newAllParemItems = reBuildParamItemFrame(getAllParamItems); ////基本参数补全项处理结果
        List<SeriesParamTypeModel> rsptmList = new ArrayList<>();
        //新能源数量
        int newEnergyNum = 0;
        //纯电动数量
        int pevSpecNum = 0;
        //燃油车数量
        int oilEnergyNum = 0;

        List<SpecDetailDto> specDetailDtoList = specDetailComponent.mGet(specIdList);

        for (Integer specid : specIdList) {
            int fuelType = 0;
            if (specDetailDtoList != null) {
                specDetailDtoList = specDetailDtoList.stream().filter(Objects::nonNull).collect(Collectors.toList());
                SpecDetailDto specDetailDto = specDetailDtoList.stream().filter(x -> x.getSpecId() == specid).findFirst().orElse(null);
                if (specDetailDto != null) {
                    fuelType = specDetailDto.getFuelType();
                }
            }
            if (newEnergyFueltypeList.contains(fuelType)) {
                newEnergyNum++;
            }
            if (fuelType == 4 || fuelType == 7) {
                pevSpecNum++;
            }
            if (noElectricMotorFuelTypeList.contains(fuelType)) {
                oilEnergyNum++;
            }
        }
        boolean allSpecIsPEV = (pevSpecNum == specIdList.size());

        for (ParamTypePo paramTypePo : allParamType) {
            //纯电不显发动机大分类
            if (allSpecIsPEV && paramTypePo.getParamTypeName().equals("发动机")) {
                continue;
            }
            //油车不显示'电动机','电池/续航','充/放电'大分类
            if (oilEnergyNum == specIdList.size() && electricTypeList.contains(paramTypePo.getParamTypeName())) {
                continue;
            }

            SeriesParamTypeModel seriesParamTypeModel = new SeriesParamTypeModel(paramTypePo.getParamTypeName(), new ArrayList<>());
            List<ParamItemPo> paramItemPoList = newAllParemItems.stream().filter(s -> s.getTypeId().equals(paramTypePo.getParamTypeId()) && s.getIsShow().equals(1)).collect(Collectors.toList());
            for (ParamItemPo paramItemPo : paramItemPoList) {
                if (paramTypePo.getParamTypeId().equals(1)) {  //基本参数大分类隐藏项
                    //全部是非新能源车型，隐藏“基本参数”的部分新能源参数
                    if (newEnergyNum == 0 && listNewEnergyParam.contains(paramItemPo.getParamName())) {
                        continue;
                    }
                    //纯电动不显示内容。
                    if (allSpecIsPEV && listNotDisPlayOfPEVCarParam.contains(paramItemPo.getParamName())) {
                        continue;
                    }
                    //燃料形式全是油的车型，不显电动机的基本参数项
                    if (oilEnergyNum == specIdList.size() &&
                            (paramItemPo.getParamName().equals("电动机(Ps)") || paramItemPo.getParamName().equals("电动机总功率(kW)") || paramItemPo.getParamName().equals("电动机总扭矩(N·m)"))
                    ) {
                        continue;
                    }

                }

                SeriesParamTypeModel.ParamitemsBean paramitemsBean = new SeriesParamTypeModel.ParamitemsBean(paramItemPo.getParamId(), paramItemPo.getParamName(), paramItemPo.getDisplayType(), new ArrayList<>(), paramItemPo.getParamSort());

                Integer currentParamItemValueEqualNullNum = 0; //统计参数项相关值是否等于空，如果此参数项是动态外显的且当前车型列表都没值 则不显示此参数项
                List<SeriesParamTypeModel.ParamitemsBean.ValueitemsBean> valueitemsBeanList = new ArrayList<>();

                Integer finalParamId = paramItemPo.getParamId();
                for (Integer specid : specIdList) {
                    Integer fuelTypeDetail = 0;
                    if (specDetailDtoList != null) {
                        SpecDetailDto specDetailDto = specDetailDtoList.stream().filter(x -> x.getSpecId() == specid).findFirst().orElse(null);
                        if (specDetailDto != null) {
                            fuelTypeDetail = specDetailDto.getFuelType();
                        }
                    }
                    //52 最大扭矩(N·m) 特殊处理
                    if (paramItemPo.getTypeId().equals(1) && paramItemPo.getParamId().equals(52)) {
                        //油电混合 插电混合 基本参数下最大扭矩用系统综合扭矩（2022年新调整）
                        if (fuelTypeDetail.equals(5) || fuelTypeDetail.equals(3)) {
                            finalParamId = 71;
                        } else if (Arrays.asList(4, 6, 7, 12).contains(fuelTypeDetail)) {
                            //基本参数下最大扭矩用 电动机总扭矩(N·m) paramId=65 （2022年新调整）
                            finalParamId = 65;
                        } else {
                            //52	最大扭矩(N·m)
                            finalParamId = 52;
                        }
                    }

                    //50 最大功率(kW) 特殊处理
                    if (paramItemPo.getTypeId().equals(1) && paramItemPo.getParamId().equals(50)) {
                        //油电混合 插电混合 基本参数下最大功率用系统综合功率 （2022年新调整） //系统综合功率(kW) 70
                        if (fuelTypeDetail.equals(5) || fuelTypeDetail.equals(3)) {
                            finalParamId = 70;
                        } else if (Arrays.asList(4, 6, 7, 12).contains(fuelTypeDetail)) {
                            //纯电、增程、氢、汽油电驱  基本参数下最大功率用电动机总功率(kW) paramId=63（2022年新调整）
                            finalParamId = 63;
                        } else {// 基本参数下最大功率用发动机最大扭矩(N·m)
                            // 基本参数下最大功率用发动机最大功率(kW)
                            //最大功率(kW) id=50
                            finalParamId = 50;

                        }
                    }

                    Integer finalParamId1 = finalParamId;
                    List<ParamUnionPo> singleParamList = paramsBySeriesid.stream().filter(s -> Integer.valueOf(s.getSpecid()).equals(specid) && s.getParamid().equals(finalParamId1))
                            .sorted(Comparator.comparingInt(ParamUnionPo::getSubParamSort)).collect(Collectors.toList());

                    SeriesParamTypeModel.ParamitemsBean.ValueitemsBean valueitemsBean = new SeriesParamTypeModel.ParamitemsBean.ValueitemsBean(specid, "", new ArrayList<>());

                    if (ListUtil.isNotEmpty(singleParamList)) {
                        //横向显示一行的参数
                        if (paramItemPo.getDisplayType() == 0) {

                            if (paramItemPo.getDataType() == 4 || paramItemPo.getDataType() == 5) {
                                //基本参数分类下的：车身结构特殊处理
                                if (paramItemPo.getTypeId().equals(1) && paramItemPo.getParamId().equals(24)) {
                                    String strDoors = ""; //车门数
                                    Optional<ParamUnionPo> doorOptional = paramsBySeriesid.stream().filter(a -> Integer.valueOf(a.getSpecid()).equals(specid) && a.getParamid() == 27)
                                            .sorted(Comparator.comparingInt(ParamUnionPo::getSubParamValue)).findFirst();
                                    if (doorOptional.isPresent()) {
                                        strDoors = doorOptional.get().getSubParamName();
                                    }
                                    String strSeatCount = "";
                                    Optional<ParamUnionPo> seatOptional = paramsBySeriesid.stream().filter(a -> Integer.valueOf(a.getSpecid()).equals(specid) && a.getParamid() == 28)
                                            .sorted(Comparator.comparingInt(ParamUnionPo::getSubParamValue)).findFirst();
                                    if (seatOptional.isPresent()) {
                                        strSeatCount = seatOptional.get().getSubParamName();
                                    }
                                    String strStruct = "";
                                    Optional<ParamUnionPo> structOptional = paramsBySeriesid.stream().filter(a -> Integer.valueOf(a.getSpecid()).equals(specid) && a.getParamid() == 24)
                                            .sorted(Comparator.comparingInt(ParamUnionPo::getSubParamValue)).findFirst();
                                    if (structOptional.isPresent()) {
                                        strStruct = structOptional.get().getSubParamName();
                                    }
                                    if (specid < 1000000) {
                                        valueitemsBean.setValue(com.autohome.app.cars.common.utils.StringUtils.format("{0}{1}{2}",
                                                StringUtils.isNotEmpty(strDoors) ? strDoors + "门" : "",
                                                StringUtils.isNotEmpty(strSeatCount) ? strSeatCount + "座" : "",
                                                StringUtils.isNotEmpty(strStruct) ? strStruct : ""));
                                    } else {
                                        valueitemsBean.setValue(com.autohome.app.cars.common.utils.StringUtils.format("{0}", StringUtils.isNotEmpty(strStruct) ? strStruct : ""));
                                    }

                                } else {
                                    valueitemsBean.setValue(singleParamList.get(0).getSubParamName());
                                }
                                //统计空值数量
                                if (singleParamList.get(0).getSubParamId() <= 0) {
                                    currentParamItemValueEqualNullNum += 1;
                                }
                            } else {
                                valueitemsBean.setValue(singleParamList.get(0).getParamValue());

                                //统计空值数量
                                if (StringUtils.isEmpty(singleParamList.get(0).getParamValue()) || singleParamList.get(0).getParamValue().equals("-") || singleParamList.get(0).getParamValue().equals("0")) {
                                    currentParamItemValueEqualNullNum += 1;
                                }
                            }

                        } else {
                            //标配项排第一位
                            List<ParamUnionPo> finanl_singleParamList = new ArrayList<>();
                            if (paramItemPo.getDataType() == 4 && singleParamList.size() > 1) {
                                finanl_singleParamList = singleParamList.stream().sorted(Comparator.comparingInt(ParamUnionPo::getSubParamValue)).collect(Collectors.toList());
                            } else {
                                finanl_singleParamList = singleParamList;

                            }
                            //统计空值数量
                            if (paramItemPo.getDataType() == 5 || paramItemPo.getDataType() == 4) {
                                if (singleParamList.size() == 0) {
                                    currentParamItemValueEqualNullNum += 1;
                                }
                            }

                            for (ParamUnionPo paramUnionPo : finanl_singleParamList) {

                                SeriesParamTypeModel.ParamitemsBean.ValueitemsBean.SublistBean sublistBean = null;
                                if (paramItemPo.getDataType() == 4) {
                                    sublistBean = new SeriesParamTypeModel.ParamitemsBean.ValueitemsBean.SublistBean("", paramUnionPo.getSubParamName(), paramUnionPo.getSubParamValue(), paramUnionPo.getPrice());
                                } else if (paramItemPo.getDataType() == 5) {
                                    sublistBean = new SeriesParamTypeModel.ParamitemsBean.ValueitemsBean.SublistBean(paramUnionPo.getSubParamName(), paramUnionPo.getSubParamTextValue(), paramUnionPo.getSubParamValue(), paramUnionPo.getPrice());
                                }
                                valueitemsBean.getSublist().add(sublistBean);
                            }
                        }
                    } else {
                        valueitemsBean.setValue("-");
                        //统计空值数量
                        currentParamItemValueEqualNullNum += 1;
                    }
                    valueitemsBeanList.add(valueitemsBean);
                }

                //当前参数项是动态外显且所有车型都没此参数项的值，整个参数项不外显
                if (paramItemPo.getDynamicShow().equals(1) && currentParamItemValueEqualNullNum.equals(specIdList.size())) {
                    continue;
                }
                paramitemsBean.getValueitems().addAll(valueitemsBeanList);
                seriesParamTypeModel.getParamitems().add(paramitemsBean);
            }
            seriesParamTypeModel.setGroupname("基本参数");
            //全是新能源车型，分组名称处理
            if (newEnergyNum > 0 && newEnergyNum == specIdList.size()) {
                if (threeElectricType.contains(seriesParamTypeModel.getName())) {
                    seriesParamTypeModel.setGroupname("三电系统");
                }
            }
            rsptmList.add(seriesParamTypeModel);
        }
        return rsptmList;
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
