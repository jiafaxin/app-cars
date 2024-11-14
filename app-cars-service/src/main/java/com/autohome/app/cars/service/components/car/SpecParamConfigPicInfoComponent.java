package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.CarSettings;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.SpecParamConfigPicInfoMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecParamConfigPicInfoEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.ParamConfigTipsGroupDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecParamConfigPicInfoDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecParamConfigPicTipDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
@DBConfig(tableName = "spec_param_config_picinfo")
public class SpecParamConfigPicInfoComponent extends BaseComponent<SpecParamConfigPicInfoDto> {

    /**
     * 新老参数对应关系
     * ParamId	ParamName	itemid
     * 90	后悬架类型	        44
     * 16	高度(mm)	            30
     * 96	前轮胎规格	        49
     * 3	整备质量(kg)	        106
     * 23	宽度(mm)	            29
     * 50	最大功率(kW)	        15
     * 30	后备厢容积(L)	        39
     * 98	备胎规格	            53
     * 17	轴距(mm)	            31
     * 52	最大扭矩(N·m)	    17
     * 14	长度(mm)	            28
     * 55	能源类型	            90
     * 2	官方0-100km/h加速(s)	97
     * 85	变速箱类型	        100
     * 75	NEDC纯电续航里程(km)	61
     */
    private final Map<Integer, Integer> map_NewParamToOldParam = new HashMap<Integer, Integer>() {{
        put(90, 44);
        put(16, 30);
        put(96, 49);
        put(3, 106);
        put(23, 29);
        put(50, 15);
        put(30, 39);
        put(98, 53);
        put(17, 31);
        put(52, 17);
        put(14, 28);
        put(55, 90);
        put(2, 97);
        put(85, 100);
        put(75, 61);
    }};

    private static String specIdParamName = "specId";

    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private SpecParamConfigPicInfoMapper specParamConfigPicInfoMapper;

    TreeMap<String, Object> makeParam(int specId) {
        return BaseComponent.ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecParamConfigPicInfoDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecParamConfigPicInfoDto>> get(List<Integer> specIdList) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    public void refreshAll(int totalMinutes,Consumer<String> xxlLog) {
        loopSpec(totalMinutes,(specId)->{
            try {
                getData(specId);
                xxlLog.accept(specId + " success ");
            } catch (Exception e) {
                xxlLog.accept(specId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
        }, xxlLog);
    }

    public void refresh(int specId) {
        try {
            getData(specId);
        } catch (Exception e) {
            log.error("刷新车型参数配置图片异常-exception:{}", e);
        }
    }

    private void getData(int specId){
        List<ParamConfigTipsGroupDto> paramConfigTipsGroup = getParamConfigTipsBySpecIds(String.valueOf(specId));
        if(ListUtil.isNotEmpty(paramConfigTipsGroup)){
            paramConfigTipsGroup.forEach(item -> {
                SpecParamConfigPicInfoDto dto = new SpecParamConfigPicInfoDto();
                dto.setSpecId(item.getSpecid());
                dto.setList(item.getList());
                update(makeParam(dto.getSpecId()), dto);
            });
        }
    }

    public List<ParamConfigTipsGroupDto> getAllParamConfigTips() {
        List<SpecParamConfigPicInfoEntity> specParamConfigPicInfoPoList = specParamConfigPicInfoMapper.getAllParamConfigTips();
        if (specParamConfigPicInfoPoList.size() > 0) {
            List<ParamConfigTipsGroupDto> paramConfigTipsGroups = new ArrayList<>();
            Map<Integer, List<SpecParamConfigPicInfoEntity>> listMap = specParamConfigPicInfoPoList.stream().collect(Collectors.groupingBy(SpecParamConfigPicInfoEntity::getSpecid));
            Map<Integer, Integer> invertMap = new HashMap<>();
            if (map_NewParamToOldParam != null && map_NewParamToOldParam.size() > 0) {
                invertMap = map_NewParamToOldParam.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            }
            for (Map.Entry<Integer, List<SpecParamConfigPicInfoEntity>> item : listMap.entrySet()) {
                ParamConfigTipsGroupDto paramConfigTipsGroup = new ParamConfigTipsGroupDto();
                paramConfigTipsGroup.setSpecid(item.getKey());
                List<SpecParamConfigPicTipDto> infoList = new ArrayList<>();
                for (SpecParamConfigPicInfoEntity sourcePo : item.getValue()) {
                    SpecParamConfigPicTipDto specParamConfigPicTipDto = new SpecParamConfigPicTipDto();

                    specParamConfigPicTipDto.setDatatype(sourcePo.getDatatype());
                    specParamConfigPicTipDto.setSpecid(sourcePo.getSpecid());
                    specParamConfigPicTipDto.setItemid(sourcePo.getItemid());
                    //新增newItemId
                    if (sourcePo.getDatatype().equals(1)) {
                        if (invertMap != null && invertMap.containsKey(sourcePo.getItemid())) {
                            specParamConfigPicTipDto.setNewItemId(invertMap.get(sourcePo.getItemid()));
                        }
                    } else {
                        specParamConfigPicTipDto.setNewItemId(sourcePo.getItemid());
                    }

                    infoList.add(specParamConfigPicTipDto);
                }
                paramConfigTipsGroup.setList(infoList);
                paramConfigTipsGroups.add(paramConfigTipsGroup);
            }
            return paramConfigTipsGroups;

        }
        return null;
    }

    public List<ParamConfigTipsGroupDto> getParamConfigTipsBySpecIds(String specIds) {
        List<SpecParamConfigPicInfoEntity> specParamConfigPicInfoPoList = specParamConfigPicInfoMapper.getParamConfigTipsBySpecIds(specIds);
        if (specParamConfigPicInfoPoList.size() > 0) {
            List<ParamConfigTipsGroupDto> paramConfigTipsGroups = new ArrayList<>();
            Map<Integer, List<SpecParamConfigPicInfoEntity>> listMap = specParamConfigPicInfoPoList.stream().collect(Collectors.groupingBy(SpecParamConfigPicInfoEntity::getSpecid));
            Map<Integer, Integer> invertMap = new HashMap<>();
            if (map_NewParamToOldParam != null && map_NewParamToOldParam.size() > 0) {
                invertMap = map_NewParamToOldParam.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            }
            for (Map.Entry<Integer, List<SpecParamConfigPicInfoEntity>> item : listMap.entrySet()) {
                ParamConfigTipsGroupDto paramConfigTipsGroup = new ParamConfigTipsGroupDto();
                paramConfigTipsGroup.setSpecid(item.getKey());
                List<SpecParamConfigPicTipDto> infoList = new ArrayList<>();
                for (SpecParamConfigPicInfoEntity sourcePo : item.getValue()) {
                    SpecParamConfigPicTipDto specParamConfigPicTipDto = new SpecParamConfigPicTipDto();

                    specParamConfigPicTipDto.setDatatype(sourcePo.getDatatype());
                    specParamConfigPicTipDto.setSpecid(sourcePo.getSpecid());
                    specParamConfigPicTipDto.setItemid(sourcePo.getItemid());
                    specParamConfigPicTipDto.setPicurl(CarSettings.getInstance().GetFullImagePath(sourcePo.getPicurl()));
                    //新增newItemId
                    if (sourcePo.getDatatype().equals(1)) {
                        if (invertMap != null && invertMap.containsKey(sourcePo.getItemid())) {
                            specParamConfigPicTipDto.setNewItemId(invertMap.get(sourcePo.getItemid()));
                        }
                    } else {
                        specParamConfigPicTipDto.setNewItemId(sourcePo.getItemid());
                    }

                    infoList.add(specParamConfigPicTipDto);
                }
                paramConfigTipsGroup.setList(infoList);
                paramConfigTipsGroups.add(paramConfigTipsGroup);
            }
            return paramConfigTipsGroups;

        }
        return null;
    }

}
