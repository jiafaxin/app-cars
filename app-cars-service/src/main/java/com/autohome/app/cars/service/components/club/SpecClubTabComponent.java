package com.autohome.app.cars.service.components.club;

import com.autohome.app.cars.apiclient.club.ClubApiClient;
import com.autohome.app.cars.apiclient.club.dtos.SeriesBbsResult;
import com.autohome.app.cars.common.utils.Constants;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.club.dtos.SpecClubTabDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * @author zhangchengtao
 * @date 2024/8/19 21:19
 */
@Component
@DBConfig(tableName = "spec_club_tab")
@Slf4j
public class SpecClubTabComponent extends BaseComponent<SpecClubTabDto> {
    @Resource
    private ClubApiClient clubApiClient;

    @Resource
    private SpecDetailComponent specDetailComponent;

    public TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create("specId", specId).build();
    }

    public SpecClubTabDto get(int specId) {
        TreeMap<String, Object> param = makeParam(specId);
        return baseGet(param);
    }


    /**
     * 刷新车型论坛帖子
     *
     * @param totalMinutes 总耗时
     * @param xxlLog       日志
     */
    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        Map<Integer, SeriesBbsResult> seriesBbsMap = new HashMap<>();
        loopSpec(totalMinutes, specId -> {
            SeriesBbsResult bbsDto;
            // 查询当前车型对应的车系
            SpecDetailDto specDetailDto = specDetailComponent.getSync(specId);
            if (Objects.isNull(specDetailDto)) {
                return;
            }
            int seriesId = specDetailDto.getSeriesId();
            if (!seriesBbsMap.containsKey(seriesId)) {
                // 查询当前车系的 BBS 信息
                bbsDto = clubApiClient.getBbsBySeriesIds(seriesId).thenApply(bbsResult -> {
                    if (Objects.nonNull(bbsResult) && Objects.nonNull(bbsResult.getResult()) && bbsResult.getResult().size() == 1) {
                        // 如果当前车系有bbs存储到map里, 避免下次再查
                        seriesBbsMap.put(seriesId, bbsResult.getResult().get(0));
                        return bbsResult.getResult().get(0);
                    } else {
                        // 如果当前车系没有bbs, 存储null 避免重复查
                        seriesBbsMap.put(seriesId, null);
                    }
                    return null;
                }).exceptionally(e -> {
                    log.warn("获取车型论坛失败", e);
                    return null;
                }).join();
            } else {
                // 如果已经查过, 直接从Map取
                bbsDto = seriesBbsMap.get(seriesId);
            }

            // bbs信息为空时直接返回
            if (Objects.isNull(bbsDto)) {
                return;
            }
            // 限制帖子时间是180天内的
            LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime localDateTime = now.minusDays(180);
            clubApiClient.getTopicListByCarSpec(specId).thenAccept(data -> {
                if (Objects.nonNull(data) && Objects.nonNull(data.getResult()) && !data.getResult().isEmpty()) {
                    boolean hasComment = data.getResult().stream().anyMatch(x -> LocalDateTime.parse(x.getPostdate(), Constants.BASIC_DATE_TIME_FORMATTER).isAfter(localDateTime));
                    if (hasComment) {
                        update(makeParam(specId), SpecClubTabDto.getInstance(data.getResult().size(), bbsDto.getBbsid(), bbsDto.getBbs(), bbsDto.getBbsname()));
                    }
                }
            });

        }, xxlLog);
    }

}
