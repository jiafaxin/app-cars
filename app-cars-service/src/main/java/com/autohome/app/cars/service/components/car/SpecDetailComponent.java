package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.enums.CarParamConfigEnum;
import com.autohome.app.cars.common.enums.EnergyTypesNewEnum;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.mapper.popauto.CarLevelMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.SpecParamMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarLevelEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecParamEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/2/29
 */
@Component
@RedisConfig
@DBConfig(tableName = "spec_detail")
@Slf4j
public class SpecDetailComponent extends BaseComponent<SpecDetailDto> {

    @Autowired
    SpecMapper specMapper;

    @Autowired
    CarLevelMapper carLevelMapper;
    @Autowired
    SpecParamMapper specParamMapper;

    static String specIdParamName = "specId";

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecDetailDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public SpecDetailDto getSync(int specId) {
        return baseGet(makeParam(specId));
    }

    public CompletableFuture<List<SpecDetailDto>> getList(List<Integer> specIds) {
        if(specIds==null||specIds.size()==0){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        List<TreeMap<String, Object>> params = specIds.stream().map(x -> makeParam(x)).collect(Collectors.toList());
        return baseGetListAsync(params).thenApply(x->{
            return x.stream().filter(y->y!=null).collect(Collectors.toList());
        });
    }

    public List<SpecDetailDto> mGet(List<Integer> specId) {
        return baseGetList(specId.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    /**
     * 从库里拉取所有数据到redis&db
     */
    public void refreshAll(Consumer<String> log) {
        // 所有级别
        List<CarLevelEntity> carLevelList = carLevelMapper.getAllLevel();
        // 所有车型
        List<SpecEntity> allSpecEntities = specMapper.getSpecAll();
        allSpecEntities.addAll(specMapper.getCvSpecAll());
        allSpecEntities = allSpecEntities.stream().filter(Objects::nonNull).collect(Collectors.toList());
        //环保标准
        List<SpecParamEntity> specStandardsList = specParamMapper.getSpecSubParamAll(CarParamConfigEnum.EMISSION_STANDARDS.getValue(), 0);
        Map<Integer, List<SpecParamEntity>> standardsListMap = specStandardsList.stream().collect(Collectors.groupingBy(SpecParamEntity::getSpecId));
        //变速箱名称
        List<SpecParamEntity> transmissionList = specParamMapper.getSpecParamAll(CarParamConfigEnum.TRANSMISSION.getValue(), 0);
        Map<Integer, List<SpecParamEntity>> transmissionListMap = transmissionList.stream().collect(Collectors.groupingBy(SpecParamEntity::getSpecId));
        //驱动类型
        List<SpecParamEntity> drivingModeList = specParamMapper.getSpecSubParamAll(CarParamConfigEnum.DRIVING_MODE_NAME.getValue(), 0);
        Map<Integer, List<SpecParamEntity>> drivingModeListMap = drivingModeList.stream().collect(Collectors.groupingBy(SpecParamEntity::getSpecId));
        //电动机总工率
        List<SpecParamEntity> electricMotorGrossPowerList = specParamMapper.getSpecParamAll(CarParamConfigEnum.ELECTRIC_MOTORGROSS_POWER.getValue(), 0);
        Map<Integer, List<SpecParamEntity>> electricMotorGrossPowerListMap = electricMotorGrossPowerList.stream().collect(Collectors.groupingBy(SpecParamEntity::getSpecId));
        //车身结构
        List<SpecParamEntity> structList = specParamMapper.getSpecSubParamAll(CarParamConfigEnum.STRUCT.getValue(), 0);
        Map<Integer, List<SpecParamEntity>> structListMap = structList.stream().collect(Collectors.groupingBy(SpecParamEntity::getSpecId));



        allSpecEntities.forEach(specEntity -> {
            try {
                update(ParamBuilder.create(specIdParamName, specEntity.getId()).build(),
                        builder(specEntity, carLevelList,standardsListMap.get(specEntity.getId()),
                                transmissionListMap.get(specEntity.getId()),
                                drivingModeListMap.get(specEntity.getId()),
                                structListMap.get(specEntity.getId()),
                                electricMotorGrossPowerListMap.get(specEntity.getId())));
            } catch (Exception e) {
                log.accept(specEntity.getId() + "fail:" + ExceptionUtil.getStackTrace(e));
            }
        });
    }

    public SpecDetailDto refresh(int specId) {
        // 所有级别
        List<CarLevelEntity> carLevelList = carLevelMapper.getAllLevel();
        // 车型
        SpecEntity specEntity;
        if (Spec.isCvSpec(specId)) {
            specEntity = specMapper.getCvSpec(specId);
        } else {
            specEntity = specMapper.getSpec(specId);
        }
        //环保标准
        List<SpecParamEntity> specStandardsList = specParamMapper.getSpecSubParamAll(CarParamConfigEnum.EMISSION_STANDARDS.getValue(), specId);

        //变速箱名称
        List<SpecParamEntity> transmissionList = specParamMapper.getSpecParamAll(CarParamConfigEnum.TRANSMISSION.getValue(), specId);

        //驱动类型
        List<SpecParamEntity> drivingModeList = specParamMapper.getSpecSubParamAll(CarParamConfigEnum.DRIVING_MODE_NAME.getValue(), specId);
        //电动机总工率
        List<SpecParamEntity> electricMotorGrossPowerList = specParamMapper.getSpecParamAll(CarParamConfigEnum.ELECTRIC_MOTORGROSS_POWER.getValue(), specId);

        //车身结构
        List<SpecParamEntity> structList = specParamMapper.getSpecSubParamAll(CarParamConfigEnum.STRUCT.getValue(), specId);

        SpecDetailDto specDetailDto = builder(specEntity, carLevelList, specStandardsList, transmissionList, drivingModeList, structList, electricMotorGrossPowerList);
        update(ParamBuilder.create(specIdParamName, specId).build(), specDetailDto);
        return specDetailDto;
    }

    private SpecDetailDto builder(SpecEntity entity, List<CarLevelEntity> carLevelList,
                                  List<SpecParamEntity> specStandardsList,
                                  List<SpecParamEntity> transmissionList,
                                  List<SpecParamEntity> drivingModeList,
                                  List<SpecParamEntity> structList,
                                  List<SpecParamEntity>electricMotorGrossPowerList) {
        CarLevelEntity carLevel = carLevelList.stream()
                .filter(x -> x.getId() == entity.getLevelId())
                .findFirst()
                .orElse(null);

        SpecDetailDto specDetailDto = new SpecDetailDto();
        specDetailDto.setBrandId(entity.getBrandId());
        specDetailDto.setBrandName(StringEscapeUtils.unescapeHtml4(entity.getBrandName()));
        specDetailDto.setBrandLogo(ImageUtils.getFullImagePath(entity.getBrandLogo()));
        specDetailDto.setFuelType(entity.getFuelType());
        specDetailDto.setEnergyTypeName(EnergyTypesNewEnum.getTypeByValue(entity.getFuelType()));
        specDetailDto.setMinPrice(entity.getMinPrice());
        specDetailDto.setMaxPrice(entity.getMaxPrice());
        specDetailDto.setLevelId(entity.getLevelId());
        specDetailDto.setLevelName(Objects.nonNull(carLevel) ? carLevel.getName() : "");
        specDetailDto.setLogo(ImageUtils.getFullImagePath(entity.getImg()));
        specDetailDto.setParamIsShow(entity.getParamIsShowByState());
        specDetailDto.setSalestate((entity.getState() == 20 || entity.getState() == 30) ? 1 : 0);
        specDetailDto.setSeriesId(entity.getSeriesId());
        specDetailDto.setSeriesName(StringEscapeUtils.unescapeHtml4(entity.getSeriesName()));
        specDetailDto.setSpecId(entity.getId());
        specDetailDto.setSpecName(StringEscapeUtils.unescapeHtml4(entity.getName()));
        specDetailDto.setState(entity.getState());
        specDetailDto.setPriceDescription(entity.getPriceDescription());
        specDetailDto.setBooked(entity.isBooked());
        specDetailDto.setTaxExemption(Objects.equals(entity.getSpecTaxType(), 2));
        specDetailDto.setDisplacement(entity.getDisplacement());
        specDetailDto.setSeats(entity.getSeats());
        if(transmissionList!=null&&transmissionList.size()>0){
            String transname = transmissionList.get(0).getParamValue();
            String gearbox="";
            if(!Arrays.asList("待查","无","-").contains(transname)){
                if(StringUtils.contains(transname,"手动")){
                    gearbox="手动";
                }else{
                    gearbox="自动";
                }
            }
            specDetailDto.setGearbox(gearbox);
        }

        specDetailDto.setStructtype(structList!=null&&structList.size()>0?structList.get(0).getSubParamName():"");
        specDetailDto.setYearName(entity.getYearName());
        specDetailDto.setYearId(entity.getYearId());
        specDetailDto.setFlowModeId(entity.getFlowMode());
        specDetailDto.setElectricKw(NumberUtils.toDouble(electricMotorGrossPowerList != null && electricMotorGrossPowerList.size() > 0 ? electricMotorGrossPowerList.get(0).getParamValue() : "",0));
        specDetailDto.setEnduranceMileage(entity.getEnduranceMileage());
        specDetailDto.setEnginePower(NumberUtils.toInt(String.valueOf(entity.getSpecEnginePower())));
        specDetailDto.setForeignCar(entity.getIsForeignCar() != null && entity.getIsForeignCar() == 1);
        specDetailDto.setImageSpec(entity.getIsImageSpec() != null && entity.getIsImageSpec() == 1);
        specDetailDto.setClassic(entity.getIsclassic() != null && entity.getIsclassic() == 1);
        specDetailDto.setDrivingModeName(drivingModeList != null && drivingModeList.size() > 0 ? drivingModeList.get(0).getSubParamName() : "");
        specDetailDto.setTransmission(transmissionList != null && transmissionList.size() > 0 ? transmissionList.get(0).getParamValue() : "");
        specDetailDto.setEmissionStandards(specStandardsList != null && specStandardsList.size() > 0 ? specStandardsList.get(0).getSubParamName() : "");
        specDetailDto.setOrders(entity.getOrders() != null ? entity.getOrders() : 0);
        specDetailDto.setSeatCount(getSeatCount(entity.getSeats()));
        specDetailDto.setIsnewcar(isNewCar(entity.getState(), entity.getTimeMarket()));
        return specDetailDto;
    }

    int getSeatCount(String seatStr) {
        try {
            if (StringUtils.isNotEmpty(seatStr)) {
                //对商用车座位数据特殊处理
                String[] seatArray = new String[]{};
                if (seatStr.indexOf("-") > -1) {
                    seatArray = seatStr.split("-");
                } else if (seatStr.indexOf("/") > -1) {
                    seatArray = seatStr.split("/");
                }
                if (seatArray != null && seatArray.length > 0) {
                    return Integer.parseInt(seatArray[seatArray.length - 1]);
                } else {
                    return Integer.parseInt(seatStr.replace("-", "0"));
                }
            }
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    boolean isNewCar(int state, String timeMarket) {
        try {
            if (state != 20 || StringUtils.isEmpty(timeMarket)) {
                return false;
            }
            Date parse = DateUtil.parse(timeMarket, "yyyy-MM-dd HH:mm:ss");
            return DateUtil.getDistanceOfTwoDate(parse, new Date()) <= 30;
        } catch (Exception e) {
        }
        return false;
    }
}
