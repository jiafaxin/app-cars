package com.autohome.app.cars.service.components.dealer;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.SeriesRecommendSpecResult;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.appcars.SeriesRecommendSpecMapper;
import com.autohome.app.cars.mapper.appcars.entities.SeriesRecommendSpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesRecommendSpecDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Component
@DBConfig(tableName = "series_recommend_spec")
@Slf4j
public class SeriesRecommendSpecComponent extends BaseComponent<SeriesRecommendSpecDto> {


    @Resource
    private DealerApiClient dealerApiClient;

    @Resource
    private SeriesRecommendSpecMapper seriesRecommendSpecMapper;

    @Resource
    private SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId)
                .add(cityParamName, cityId)
                .build();
    }

    /**
     * 供carext调用查询默认询价车型数据
     *
     * @param paramMap 参数Map
     * @return 默认询价车型List
     */
    public String getListBySeriesIds(TreeMap<String, Object> paramMap) {
        if (!paramMap.containsKey("cityId") && !paramMap.containsKey("seriesIds")) {
            log.warn("参数异常，cityId:{},seriesIds:{}", paramMap.get("cityId"), paramMap.get("seriesIds"));
            return StrPool.EMPTY;
        }
        String cityIdStr = paramMap.get("cityId").toString();
        String seriesIds = paramMap.get("seriesIds").toString();
        if (!StringUtils.hasLength(cityIdStr) || !StringUtils.hasLength(seriesIds)) {
            log.warn("参数异常，cityId:{},seriesIds:{}", cityIdStr, seriesIds);
            return StrPool.EMPTY;
        }
        int cityId = Integer.parseInt(cityIdStr);
        List<Integer> seriesIdList = Arrays.stream(seriesIds.split(StrPool.COMMA)).map(Integer::parseInt).toList();
        List<TreeMap<String, Object>> paramList = new ArrayList<>(seriesIdList.size());
        for (Integer seriesId : seriesIdList) {
            TreeMap<String, Object> param = makeParam(seriesId, cityId);
            paramList.add(param);
        }
        List<SeriesRecommendSpecDto> resultList = baseGetList(paramList);
        resultList.removeIf(Objects::isNull);
        return JSONObject.toJSONString(resultList);
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopOnSaleSeriesCity(totalMinutes, (seriesId, cityIdList) -> {

            Map<TreeMap<String, Object>, SeriesRecommendSpecDto> map = new HashMap<>(cityIdList.size());
            for (Integer cityId : cityIdList) {
                seriesCityAskPriceNewComponent.get(seriesId, cityId).thenAccept(p -> {
                    if (Objects.nonNull(p) && p.getMinPrice() > 0) {
                        ThreadUtil.sleep(25);
                        dealerApiClient.getOrderRecommendSeriesSpec(cityId, seriesId).thenAccept(data -> {
                            if (Objects.nonNull(data)) {
                                SeriesRecommendSpecResult result = data.getResult();
                                if (result.getSpecId() != 0) {
                                    SeriesRecommendSpecDto entity = new SeriesRecommendSpecDto();
                                    entity.setSeriesId(result.getSeriesId());
                                    entity.setCityId(cityId);
                                    entity.setSpecId(result.getSpecId());
                                    map.put(makeParam(seriesId, cityId), entity);
                                }
                            }
                        }).join();
                    }
                }).join();
            }
            if (!map.isEmpty()) {
                updateBatch(map);
                map.clear();
            }

        }, xxlLog);
    }

}
