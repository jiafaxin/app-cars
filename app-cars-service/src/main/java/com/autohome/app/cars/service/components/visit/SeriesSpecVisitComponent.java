package com.autohome.app.cars.service.components.visit;

import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.appcars.DwHiveSeriesVisitMapper;
import com.autohome.app.cars.mapper.appcars.entities.SeriesSpecVisitEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.visit.dtos.SeriesSpecVisitDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/5/8
 */
@Slf4j
@Component
@DBConfig(tableName = "series_spec_visit")
public class SeriesSpecVisitComponent extends BaseComponent<SeriesSpecVisitDto> {

    static String paramName = "seriesId";

    @Autowired
    DwHiveSeriesVisitMapper dwHiveMapper;


    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesSpecVisitDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> logInfo) {
        String todayDt = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        List<SeriesSpecVisitEntity> specVisitEntityList = dwHiveMapper.getAllSpecVisitByDt(todayDt);
        if (CollectionUtils.isEmpty(specVisitEntityList)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            String yesterdayDt = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
            specVisitEntityList = dwHiveMapper.getAllSpecVisitByDt(yesterdayDt);
        }
        if (CollectionUtils.isEmpty(specVisitEntityList)) {
            logInfo.accept("无待计算的车系数据");
            return;
        }

        // 所有的待更新数据
        Map<TreeMap<String, Object>, SeriesSpecVisitDto> datas = new HashMap<>();
        // 本次新计算得到的key，用于删除历史数据
        Set<String> newKeys = new HashSet<>();

        AtomicBoolean exFlag = new AtomicBoolean(false);
        specVisitEntityList.forEach(se -> {
            try {
                SeriesSpecVisitDto visitDto = new SeriesSpecVisitDto();
                visitDto.setSeriesName(se.getSeries_name());
                visitDto.setDt(se.getDt());
                visitDto.setAllUv(se.getAll_uv());
                visitDto.setVisitList(new ArrayList<>());
                List<SpecUvEntity> specUvEntityList =
                        JsonUtil.toObjectList(se.getSpec_uv(), SpecUvEntity.class);
                specUvEntityList.forEach(specUv -> {
                    SeriesSpecVisitDto.SpecVisit visitInfo = new SeriesSpecVisitDto.SpecVisit();
                    visitInfo.setSpecId(specUv.getSpec_id());
                    visitInfo.setSpecName(specUv.getSpec_name());
                    visitInfo.setPrice(CommonHelper.getPriceInfo(specUv.getSpec_price()));
                    visitInfo.setUv(specUv.getUv());
                    visitDto.getVisitList().add(visitInfo);
                });
                visitDto.setVisitList(visitDto.getVisitList().stream()
                        .sorted(Comparator.comparing(SeriesSpecVisitDto.SpecVisit::getUv).reversed())
                        .collect(Collectors.toList()));
                datas.put(makeParam(se.getSeries_id()), visitDto);
                newKeys.addAll(datas.keySet().stream().map(this::getKey).collect(Collectors.toSet()));
            } catch (Exception e) {
                exFlag.set(true);
                log.error("数据添加失败, ve={}, e={}", se, e);
                logInfo.accept(String.format("数据添加失败, ve=%s, e=%s", se, e));
            }
        });

        // 批量更新数据
        updateBatch(datas);

        // 删除历史无效数据
        if (!exFlag.get()) {
            log.error("SeriesSpecVisitComponent 数据同步任务出现异常，不执行历史数据删除任务");
            deleteHistorys(new HashSet<>(newKeys), logInfo);
        }
    }

    public void deleteHistorys(HashSet<String> newKeys, Consumer<String> log) {
        deleteHistory(newKeys, log);
    }

    @Data
    private static class SpecUvEntity {
        private int spec_id;
        private String spec_name;
        private int uv;
        private int spec_price;
    }

}