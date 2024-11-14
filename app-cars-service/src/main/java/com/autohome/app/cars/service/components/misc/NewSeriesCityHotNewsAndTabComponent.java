package com.autohome.app.cars.service.components.misc;

import com.autohome.app.cars.apiclient.opscard.OpsCardApiClient;
import com.autohome.app.cars.apiclient.opscard.dtos.NewSeriesCityHotNewsAndTabResult;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.misc.dtos.NewSeriesCityHotNewsAndTabDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 新版车系页热点数据Component
 * <a href="https://doc.autohome.com.cn/docapi/page/share/share_xonpSFZvhQ"/>需求地址</a>
 *
 * @author zhangchengtao
 * @date 2024/8/22 14:11
 */
@Component
@DBConfig(tableName = "new_series_city_tab_hot_news")
public class NewSeriesCityHotNewsAndTabComponent extends BaseComponent<List<NewSeriesCityHotNewsAndTabDto>> {


    @Resource
    private OpsCardApiClient opsCardApiClient;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";


    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public List<NewSeriesCityHotNewsAndTabDto> get(int seriesId, int cityId) {
        return baseGet(makeParam(seriesId, cityId));
    }

    public CompletableFuture<List<NewSeriesCityHotNewsAndTabDto>> getAsync(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinute, Consumer<String> xxlLog) {
        Set<String> newKeys = Collections.synchronizedSet(new HashSet<>());
        loopCity(totalMinute, cityId -> {
            // 当前城市下所有车系的卡片数据
            List<NewSeriesCityHotNewsAndTabDto> seriseDtoList = new ArrayList<>();
            // 查询卡片
            opsCardApiClient.getSeriesHotNewsAndTab(cityId).thenAccept(result -> {
                if (Objects.nonNull(result)
                        && Objects.nonNull(result.getResult())
                        && !CollectionUtils.isEmpty(result.getResult().getCards())) {
                    List<NewSeriesCityHotNewsAndTabResult.Card> cardList = result.getResult().getCards();
                    for (NewSeriesCityHotNewsAndTabResult.Card card : cardList) {
                        // 只取 new_series_hot_news 卡片的数据
                        if (!card.getCardtag().equals("new_series_hot_news")) {
                            continue;
                        }
                        List<NewSeriesCityHotNewsAndTabResult.Card.Cell> cells = card.getCells();
                        // 每个车系创建一个对象
                        for (NewSeriesCityHotNewsAndTabResult.Card.Cell cell : cells) {
                            if (StringUtils.hasLength(cell.getSeriesids())) {
                                String[] seriesIds = cell.getSeriesids().split(StrPool.COMMA);
                                for (String seriesId : seriesIds) {
                                    // 创建车系对象
                                    seriseDtoList.add(getNewSeriesCityHotNewsAndTabDto(cell, seriesId, cityId));
                                }
                            }
                        }
                    }
                }
            }).exceptionally(e -> {
                xxlLog.accept("获取新版车系页热点数据异常:" + e.getMessage());
                return null;
            }).join();
            // 批量保存
            if (!seriseDtoList.isEmpty()) {
                // 按车系分组
                Map<Integer, List<NewSeriesCityHotNewsAndTabDto>> grpBySeriesMap = seriseDtoList.stream().collect(Collectors.groupingBy(NewSeriesCityHotNewsAndTabDto::getSeriesId));
                // 转换为批量保存的Map
                Map<TreeMap<String, Object>, List<NewSeriesCityHotNewsAndTabDto>> map = new HashMap<>(grpBySeriesMap.size());
                grpBySeriesMap.forEach((seriesId, list) -> {
                    TreeMap<String, Object> param = makeParam(seriesId, cityId);
                    map.put(param, list);
                    newKeys.add(getKey(param));
                });
                // 批量保存
                xxlLog.accept(String.format("当前城市ID:%d, 更新数量:%d", cityId, map.size()));
                updateBatch(map);
            }
        }, xxlLog);

        deleteHistory(new HashSet<>(newKeys), xxlLog);
    }

    private static NewSeriesCityHotNewsAndTabDto getNewSeriesCityHotNewsAndTabDto(NewSeriesCityHotNewsAndTabResult.Card.Cell cell, String seriesId, Integer cityId) {
        NewSeriesCityHotNewsAndTabDto dto = new NewSeriesCityHotNewsAndTabDto();
        dto.setIcon(cell.getIcon());
        dto.setSort(Integer.parseInt(cell.getSort()));
        dto.setTitle(cell.getTitle());
        dto.setType(cell.getType());
        dto.setSeriesId(Integer.parseInt(seriesId));
        dto.setSubtitle(cell.getSubtitle());
        dto.setLinkUrl(cell.getLinkurl());
        dto.setPosition(cell.getPosition());
        dto.setRedDot(cell.getReddot());
        dto.setCityId(cityId);
        dto.setPageCardDataId(cell.getPagecarddataid());
        return dto;
    }

}
