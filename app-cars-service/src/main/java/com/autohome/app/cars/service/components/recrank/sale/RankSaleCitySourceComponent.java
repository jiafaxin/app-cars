package com.autohome.app.cars.service.components.recrank.sale;

import com.autohome.app.cars.mapper.appcars.RankSaleCityMapper;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleCitySourceEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class RankSaleCitySourceComponent extends BaseComponent<RankSaleCitySourceEntity> {
    @Resource
    private RankSaleCityMapper rankSaleCityMapper;


    public List<RankSaleCitySourceEntity> getListByCondition(String beginMonth, String endMonth, String cityId, Integer size) {
        if (Objects.isNull(size)) {
            size = 10000;
        }
        if (size == 0) {
            return null;
        }
        // 如果起止月份为空, 则使用最新月份
        if (!StringUtils.hasLength(beginMonth) || !StringUtils.hasLength(endMonth)) {
            RankSaleCitySourceEntity lastMonth = rankSaleCityMapper.getLastMonth();
            if (Objects.nonNull(lastMonth) && StringUtils.hasLength(lastMonth.getMonth())) {
                beginMonth = lastMonth.getMonth();
                endMonth = lastMonth.getMonth();
            }
        }
        // 获取当月销量数据
        List<RankSaleCitySourceEntity> curCitySourceResultList = rankSaleCityMapper.getSaleCountByCondition(beginMonth, endMonth, cityId, size);
        if (Objects.nonNull(curCitySourceResultList) && !curCitySourceResultList.isEmpty()) {
            return curCitySourceResultList;
        }
        return Collections.emptyList();
    }



}
