package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.mapper.popauto.SeriesOtaOwnerMapper;
import com.autohome.app.cars.mapper.popauto.entities.ChargeStationModel;
import com.autohome.app.cars.mapper.popauto.entities.OtaUpgradeModel;
import com.autohome.app.cars.mapper.popauto.entities.OwnerRightsModel;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesOtaOwnerDto;
import com.autohome.app.cars.service.components.owner.dtos.SeriesOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 车系图片信息
 */
@Component
@DBConfig(tableName = "series_otaowner")
@RedisConfig(keyVersion = "v2")
public class SeriesOtaOwnerComponent extends BaseComponent<SeriesOtaOwnerDto> {
    final String seriesPicIdParamName = "seriesId";

    @Autowired
    SeriesOtaOwnerMapper seriesOtaOwnerMapper;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesPicIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesOtaOwnerDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeries(totalMinutes, seriesId -> {
            try {
                List<OtaUpgradeModel> otaUpgrades = seriesOtaOwnerMapper.getOtaUpgrades(seriesId, null, 1, 1);
                List<OwnerRightsModel> ownerRights = seriesOtaOwnerMapper.getOwnerRights(seriesId, 0);
                List<ChargeStationModel> chargeStations = seriesOtaOwnerMapper.getChargeStations(seriesId, 0);

                SeriesOtaOwnerDto dto = new SeriesOtaOwnerDto();
                if (otaUpgrades.size() > 0) {
                    dto.setOtaFlag(1);
                    dto.setOtaPushTime(otaUpgrades.get(0).getPushTime());
                    HashMap<String, Integer> statistics = seriesOtaOwnerMapper.getOtaUpgradeStatistics(seriesId);
                    dto.setOtaHistoryCount(statistics.get("historyCount"));
                }
                if (ownerRights.size() > 0) {
                    dto.setOwnerFlag(1);
                    dto.setAllOwnerCount(ownerRights.size());
    //                dto.setOwner0Count((int) ownerRights.stream().filter(p -> p.getType() == 0).count());
    //                dto.setOwner1Count((int) ownerRights.stream().filter(p -> p.getType() == 1).count());
    //                dto.setOwner2Count((int) ownerRights.stream().filter(p -> p.getType() == 2).count());
    //                dto.setOwner3Count((int) ownerRights.stream().filter(p -> p.getType() == 3).count());
                }
                if (chargeStations.size() > 0) {
                    dto.setChargeStationFlag(1);
                    dto.setChargeStationPayName(chargeStations.get(0).getPayTypeName());
                    dto.setChargeStationName(chargeStations.get(0).getName());
                }
                update(makeParam(seriesId), dto);
            } catch (Exception e) {
                xxlLog.accept(String.format("seriesid:%s,error:%s",seriesId,e.getMessage()));
            }

        }, xxlLog);
    }
}
