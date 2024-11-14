package com.autohome.app.cars.service.components.recrank.sale;

import com.autohome.app.cars.mapper.appcars.RankSaleMonthMapper;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleMonthSourceEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchengtao
 * @date 2024/4/23 10:46
 */
@Component
@Slf4j
@DBConfig(tableName = "rank_sale_month")
public class RankSaleMonthSourceComponent extends BaseComponent<RankSaleMonthSourceEntity> {

    @Resource
    private RankSaleMonthMapper rankSaleMonthMapper;



    public List<RankSaleMonthSourceEntity> getSaleCountByCondition(String beginMonth, String endMonth, Integer size) {
        if (Objects.isNull(size)) {
            size = 10000;
        }
        if (size == 0) {
            return null;
        }
        // 如果起止月份为空, 则使用最新月份
        if (!StringUtils.hasLength(beginMonth) || !StringUtils.hasLength(endMonth)) {
            RankSaleMonthSourceEntity lastMonth = rankSaleMonthMapper.getLastMonth();
            if (Objects.nonNull(lastMonth) && StringUtils.hasLength(lastMonth.getMonth())) {
                beginMonth = lastMonth.getMonth();
                endMonth = lastMonth.getMonth();
            }
        }
        // 获取当月销量数据
        List<RankSaleMonthSourceEntity> curMonthSourceResultList = rankSaleMonthMapper.getSaleCountByCondition(beginMonth, endMonth, size);
        if (Objects.nonNull(curMonthSourceResultList) && !curMonthSourceResultList.isEmpty()) {
            return curMonthSourceResultList;
        }
        return Collections.emptyList();
    }

    /**
     * 获取最新月
     *
     * @return
     */
    public String getLastMonth() {
        RankSaleMonthSourceEntity lastMonth = rankSaleMonthMapper.getLastMonth();
        if (Objects.nonNull(lastMonth) && StringUtils.hasLength(lastMonth.getMonth())) {
            return lastMonth.getMonth();
        }
        return null;
    }

}
