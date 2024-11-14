package com.autohome.app.cars.service.components.hangqing;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.hangqing.dtos.SeriesSortConditionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;

/**
 * @author chengjincheng
 * @date 2024/7/25
 */
@Slf4j
@Component
public class CitySortHangqingTempComponent extends BaseComponent<List<SeriesSortConditionDto>> {

    final static String sortParamName = "sort";
    final static String cityParamName = "cityId";

    @Autowired
    private CitySortHangqingComponent citySortHangqingComponent;


    TreeMap<String, Object> makeParam(int cityId, int sort) {
        return ParamBuilder.create(cityParamName, cityId).add(sortParamName, sort).build();
    }

    public String get(TreeMap<String, Object> params) {
        List<SeriesSortConditionDto> dto = get((int) params.get("cityId"));
        return JsonUtil.toString(dto);
    }

    public List<SeriesSortConditionDto> get(int cityId) {
        return citySortHangqingComponent.getSeriesSortDtoList(cityId);
    }
}
