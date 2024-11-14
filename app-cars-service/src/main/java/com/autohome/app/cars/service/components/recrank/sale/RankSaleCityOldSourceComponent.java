package com.autohome.app.cars.service.components.recrank.sale;

import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.mapper.appcars.RankSaleCityOldMapper;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleCitySourceEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class RankSaleCityOldSourceComponent extends BaseComponent<RankSaleCitySourceEntity> {
    @Resource
    private RankSaleCityOldMapper rankSaleCityOldMapper;


    public List<RankSaleCitySourceEntity> getListByCondition(String beginMonth, String endMonth, Integer cityId, Integer size) {
        if (Objects.isNull(size)) {
            size = 10000;
        }
        if (size == 0) {
            return Collections.emptyList();
        }
        // 如果起止月份为空, 则使用最新月份
        if (!StringUtils.hasLength(beginMonth) || !StringUtils.hasLength(endMonth)) {
            RankSaleCitySourceEntity lastMonth = rankSaleCityOldMapper.getLastMonth();
            if (Objects.nonNull(lastMonth) && StringUtils.hasLength(lastMonth.getMonth())) {
                beginMonth = lastMonth.getMonth();
                endMonth = lastMonth.getMonth();
            }
        }
        // 获取当月销量数据
        List<RankSaleCitySourceEntity> curCitySourceResultList = rankSaleCityOldMapper.getSaleCountByCondition(beginMonth, endMonth, cityId, size);
        if (Objects.nonNull(curCitySourceResultList) && !curCitySourceResultList.isEmpty()) {
            return curCitySourceResultList;
        }
        return Collections.emptyList();
    }

    public RankSaleCitySourceEntity getLastOneByMonth(String month) {
        return rankSaleCityOldMapper.getLastOneByMonth(month);
    }

    /**
     * 获取城市榜最新月份
     * @return 最新月份
     */
    public String getLastMonth() {
        RankSaleCitySourceEntity lastMonth = rankSaleCityOldMapper.getLastMonth();
        if (Objects.nonNull(lastMonth)) {
            return lastMonth.getMonth();
        }
        return StrPool.EMPTY;
    }


    public List<RankSaleCitySourceEntity> getAllCityByMonth(String month, int isNewEnergy) {
        return rankSaleCityOldMapper.getAllCityByMonth(month, isNewEnergy);
    }
}
