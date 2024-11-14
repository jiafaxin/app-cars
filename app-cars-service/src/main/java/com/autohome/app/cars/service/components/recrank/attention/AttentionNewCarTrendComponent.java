package com.autohome.app.cars.service.components.recrank.attention;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.appcars.AttRankNewCarMapper;
import com.autohome.app.cars.mapper.appcars.entities.AttRankNewCarEntity;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.recrank.dtos.AttentionNewCarTrendDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * @date 2024/3/27
 */
@Slf4j
@Component
public class AttentionNewCarTrendComponent extends BaseComponent<AttentionNewCarTrendDto> {

    final static String seriesIdParamName = "seriesId";

    @Autowired
    private AttRankNewCarMapper attRankNewCarMapper;

    @Autowired
    private SeriesMapper seriesMapper;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public String get(TreeMap<String, Object> params) {
        AttentionNewCarTrendDto dto = get((int) params.get("seriesId")).join();
        return JsonUtil.toString(dto);
    }

    public CompletableFuture<AttentionNewCarTrendDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> logInfo) {
        loopSeries(1, seriesId -> {
            AttentionNewCarTrendDto dto = buildTrendDto(seriesId);
            if (dto == null) {
                return;
            }
            update(makeParam(seriesId), dto);
        }, logInfo);
    }

    protected void loopSeries(int totalMinutes, Consumer<Integer> execute, Consumer<String> logInfo) {
        String todayDt = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        List<Integer> seriesIds = attRankNewCarMapper.getSeriesIdsByDt(todayDt);
        if (CollectionUtils.isEmpty(seriesIds)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            String yesterdayDt = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
            seriesIds = attRankNewCarMapper.getSeriesIdsByDt(yesterdayDt);
        }
        // TODO chengjincheng 2024/10/14 昨天没数据，热修代码
        if (CollectionUtils.isEmpty(seriesIds)) {
            // 兜底逻辑，防止过0点后，数据组的数据还没有生产出来，此时取前一天的数据进行兜底
            String yesterdayDt = DateFormatUtils.format(DateUtils.addDays(new Date(), -2), "yyyy-MM-dd");
            seriesIds = attRankNewCarMapper.getSeriesIdsByDt(yesterdayDt);
        }
        if (CollectionUtils.isEmpty(seriesIds)) {
            logInfo.accept("无待计算的车系数据");
            return;
        }
        int duration = totalMinutes * 60000 / seriesIds.size();
        logInfo.accept(String.format("总计：%s 个车系，总运行时间预计：%s m，运行区间：%s ms",
                seriesIds.size(), totalMinutes, duration));

        long s = System.currentTimeMillis();
        int count = 1;
        for (Integer seriesId : seriesIds) {
            CompletableFuture.runAsync(() -> execute.accept(seriesId), ThreadPoolUtils.defaultThreadPoolExecutor);
            ThreadUtil.sleep(duration);
            if (count++ % 100 == 0) {
                logInfo.accept("now:" + count);
            }
        }
        List<Integer> seriesIdList = seriesMapper.getAllSeriesIds().stream().distinct().toList();
        List<TreeMap<String, Object>> keys = new ArrayList<>();
        List<Integer> finalSeriesIds = seriesIds;
        seriesIdList.stream().filter(e -> !finalSeriesIds.contains(e))
                .forEach(seriesId -> keys.add(makeParam(seriesId)));
        deleteRedis(keys);

        logInfo.accept(String.format("success，总耗时：%s s", (System.currentTimeMillis() - s) / 1000));
    }


    private AttentionNewCarTrendDto buildTrendDto(int seriesId) {
        try {
            List<AttRankNewCarEntity> attRankNewCarEntityList = attRankNewCarMapper.get30DaysBySeriesId(seriesId);
            if (CollectionUtils.isEmpty(attRankNewCarEntityList)) {
                return null;
            }

            int currentRankNum = attRankNewCarEntityList.get(0).getRankNum();
            String seriesName = attRankNewCarEntityList.get(0).getSeriesName();

            attRankNewCarEntityList = attRankNewCarEntityList.stream()
                    .sorted(Comparator.comparing(AttRankNewCarEntity::getDt))
                    .toList();

            AttentionNewCarTrendDto dto = new AttentionNewCarTrendDto();
            dto.setSeriesId(seriesId);
            dto.setSeriesName(seriesName);
            dto.setTitle(seriesName + "关注度趋势");
            dto.setChartColNum(Math.min(attRankNewCarEntityList.size(), 7));
            dto.setCurrentRankNum(currentRankNum);
            int max = 0;
            for (AttRankNewCarEntity attRankNewCarEntity : attRankNewCarEntityList) {
                AttentionNewCarTrendDto.TrendDto trendDTO = new AttentionNewCarTrendDto.TrendDto();
                trendDTO.setRank(attRankNewCarEntity.getRankNum() + "名");
                trendDTO.setRankNum(attRankNewCarEntity.getRankNum());
                trendDTO.setAttNum(attRankNewCarEntity.getAtt());
                max = Math.max(max, trendDTO.getRankNum());
                dto.getTrendList().add(trendDTO);

                AttentionNewCarTrendDto.MonthDayDto monthDayDTO = new AttentionNewCarTrendDto.MonthDayDto();
                monthDayDTO.setDate(DateUtils.parseDate(attRankNewCarEntity.getDt(), "yyyy-MM-dd"));
                monthDayDTO.setDateFormatStr(DateFormatUtils.format(monthDayDTO.getDate(), "MM/dd"));
                dto.getMonth().add(monthDayDTO);
            }
            dto.setMax(max);

            return dto;
        } catch (Exception e) {
            log.error("新车关注度排名趋势组件构建dto异常, seriesId={}", seriesId, e);
        }
        return null;
    }


}
