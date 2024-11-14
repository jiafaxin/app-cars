package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.koubei.KoubeiApiClient;
import com.autohome.app.cars.apiclient.koubei.dtos.KoubeiInfoResult;
import com.autohome.app.cars.apiclient.vr.VrApiClient;
import com.autohome.app.cars.apiclient.vr.dtos.CockpitVrResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecParamMapper;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecParamEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesEnergyInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author : zzli
 * @description : 新能源车系的一些属性数据，比如：官方续航、快/慢充时间、电池容量等
 * 0 0 0/2 * * ?   每两小时执行一次
 * @date : 2024/2/26 14:11
 */
@Component
@DBConfig(tableName = "series_energy_info")
public class SeriesEnergyInfoComponent extends BaseComponent<SeriesEnergyInfoDto> {
    final static String seriesIdParamName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesEnergyInfoDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    @Autowired
    SpecParamMapper specParamMapper;
    @Autowired
    SeriesMapper seriesMapper;
    @Autowired
    KoubeiApiClient koubeiApiClient;
    @Autowired
    VrApiClient vrApiClient;

    public void refreshAll(Consumer<String> log) {
        //所有新能源车系
        List<SeriesEntity> allEVSeries = seriesMapper.getgetAllEnergySeries();

        for (SeriesEntity series : allEVSeries) {
            //官方续航等
            SeriesEnergyInfoDto.ElectricAttributes specParamValue = getSpecParamValue(series);
            //是否有口碑数据
            Boolean isShowEVSugarBeans = checkHasKouBeiInfoList(series.getId()).join();
            //超测数据
            CockpitVrResult cockpitVrResult = null;
            BaseModel<CockpitVrResult> cockpitVrResultBaseModel = vrApiClient.getCockpitVrInfo(series.getId()).join();
            if (cockpitVrResultBaseModel != null && cockpitVrResultBaseModel.getResult() != null && cockpitVrResultBaseModel.getReturncode() == 0) {
                cockpitVrResult = cockpitVrResultBaseModel.getResult();
            }
            if (specParamValue != null || isShowEVSugarBeans || cockpitVrResult != null) {
                SeriesEnergyInfoDto dto = new SeriesEnergyInfoDto();
                dto.setSeriesName(series.getName());
                dto.setElectricAttributes(specParamValue);
                dto.setShowEVSugarBeans(isShowEVSugarBeans);
                dto.setCockpitVrResult(cockpitVrResult);
                update(makeParam(series.getId()), dto);
                log.accept("success,车系id:" + series.getId());
            } else {
                delete(makeParam(series.getId()));
                log.accept("delete,车系id:" + series.getId());
            }
            ThreadUtil.sleep(50);
        }
    }

    /*官方续航：新能源车系下优先在售车型，没在售取停售，续航:CLTC>NEDC>WLTC  ，同标准排序取最大值；
    快、慢充时间：新能源车系下优先在售车型，没在售取停售  。快：取时间最短，慢：取时间最长
    电池能量：新能源车系下优先在售车型，没在售取停售，取最大车型的值
    四个数值之间与车型不关联，先取车系下 在售车型，没有取停售，各自项目下的最优值*/
    SeriesEnergyInfoDto.ElectricAttributes getSpecParamValue(SeriesEntity series) {
        List<SpecParamEntity> paramEntities = specParamMapper.getSpecPartParamBySeriesId(series.getId(), series.getLevelId());
        if (CollectionUtils.isEmpty(paramEntities)) {
            return null;
        }
        List<String> specState = getSpecState(paramEntities);

        if (specState.isEmpty()) {
            return null;
        }
        List<SpecParamEntity> specStateParamList = paramEntities.stream().filter(x -> specState.get(0).indexOf(x.getSpecState() + "") > -1).collect(Collectors.toList());

        SeriesEnergyInfoDto.ElectricAttributes attributes = new SeriesEnergyInfoDto.ElectricAttributes();
        SpecParamEntity maxBatteryCapacity = getMaxValue(specStateParamList, 76);
        //电池能量
        if (maxBatteryCapacity != null) {
            attributes.setBatteryCapacity(NumberUtils.toDouble(maxBatteryCapacity.getParamValue()) + "kWh");
        }

        //续航:CLTC>NEDC>WLTC
        //101	CLTC纯电续航里程(km)
        //75	NEDC纯电续航里程(km)
        //135	WLTC纯电续航里程(km)
        SpecParamEntity maxMileageValue = getMaxValue(specStateParamList, 101);
        if (maxMileageValue != null) {
            attributes.setEnduranceMileage(NumberUtils.toInt(maxMileageValue.getParamValue()) + "KM");
        }
        if (StringUtils.isEmpty(attributes.getEnduranceMileage())) {
            maxMileageValue = getMaxValue(specStateParamList, 75);
            if (maxMileageValue != null) {
                attributes.setEnduranceMileage(NumberUtils.toInt(maxMileageValue.getParamValue()) + "KM");
            }
        }
        if (StringUtils.isEmpty(attributes.getEnduranceMileage())) {
            maxMileageValue = getMaxValue(specStateParamList, 135);
            if (maxMileageValue != null) {
                attributes.setEnduranceMileage(NumberUtils.toInt(maxMileageValue.getParamValue()) + "KM");
            }
        }
        // 快充时间
        SpecParamEntity a = getMinValue(specStateParamList, 79);
        if (a != null) {
            attributes.setFastChargetime(NumberUtils.toDouble(a.getParamValue()) + "小时");
        }
        //慢充时间
        SpecParamEntity b = getMaxValue(specStateParamList, 81);
        if (b != null) {
            attributes.setSlowChargetime(NumberUtils.toDouble(b.getParamValue()) + "小时");
        }
        return attributes;
    }


    SpecParamEntity getMaxValue(List<SpecParamEntity> paramEntityList, int paramId) {
        return paramEntityList.stream()
                .filter(specItem -> specItem.getParamId() == paramId && NumberUtils.toDouble(specItem.getParamValue()) > 0)
                .max(Comparator.comparing(item -> NumberUtils.toDouble(item.getParamValue()))).orElse(null);
    }

    SpecParamEntity getMinValue(List<SpecParamEntity> paramEntityList, int paramId) {
        return paramEntityList.stream()
                .filter(specItem -> specItem.getParamId() == paramId && NumberUtils.toDouble(specItem.getParamValue()) > 0)
                .min(Comparator.comparing(item -> NumberUtils.toDouble(item.getParamValue()))).orElse(null);
    }

    List<String> getSpecState(List<SpecParamEntity> itemList) {
        List<String> specState = new ArrayList<>();
        if (itemList == null || itemList.size() == 0) {
            return specState;
        }
        if (itemList.stream().anyMatch(specItem -> specItem.getSpecState() == 20 || specItem.getSpecState() == 30)) {
            specState.add("20,30");
        } else if (itemList.stream().anyMatch(specItem -> specItem.getSpecState() == 40)) {
            specState.add("40");
        }
        return specState;
    }

    /**
     * 是否显示新能源车系，真实续航糖豆
     *
     * @return
     */
    CompletableFuture<Boolean> checkHasKouBeiInfoList(Integer seriesId) {
        return koubeiApiClient.getKouBeiInfoList(seriesId, 1, 4, 1).thenComposeAsync(satisfy -> {
            if (Objects.nonNull(satisfy) && Objects.nonNull(satisfy.getResult()) && CollectionUtils.isNotEmpty(satisfy.getResult().getList())) {
                return CompletableFuture.completedFuture(true);
            }
            return koubeiApiClient.getKouBeiInfoList(seriesId, 6, 2, 1).thenApply(battery -> {
                if (Objects.nonNull(battery) && Objects.nonNull(battery.getResult()) && CollectionUtils.isNotEmpty(battery.getResult().getList())) {
                    return true;
                }
                return false;
            });
        });
    }
}
