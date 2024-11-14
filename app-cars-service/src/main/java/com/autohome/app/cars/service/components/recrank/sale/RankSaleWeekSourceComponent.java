package com.autohome.app.cars.service.components.recrank.sale;

import com.autohome.app.cars.mapper.appcars.RankSaleWeekMapper;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleMonthSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleWeekSourceEntity;
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
 * @date 2024/5/6 11:15
 */
@Component
@Slf4j
@DBConfig(tableName = "rank_sale_week")
public class RankSaleWeekSourceComponent extends BaseComponent<RankSaleWeekSourceEntity> {

    @Resource
    private RankSaleWeekMapper rankSaleWeekMapper;

    /**
     * 通过日期查询周销量
     *
     * @param beginWeek 开始周销量
     * @param endWeek   结束销量周
     * @param size      数据大小
     * @return 周销量
     */
    public List<RankSaleWeekSourceEntity> getListByWeek(String beginWeek, String endWeek, Integer size) {
        if (Objects.isNull(size)) {
            size = 10000;
        }
        if (size == 0) {
            return null;
        }
        // 如果起止周为空, 则使用最新周
        if (!StringUtils.hasLength(beginWeek) || !StringUtils.hasLength(endWeek)) {
            RankSaleWeekSourceEntity lastWeek = rankSaleWeekMapper.getLastWeek();
            if (Objects.nonNull(lastWeek) && StringUtils.hasLength(lastWeek.getWeek_day())) {
                beginWeek = lastWeek.getWeek_day();
                endWeek = lastWeek.getWeek_day();
            }
        }
        // 获取当月销量数据
        List<RankSaleWeekSourceEntity> curWeekSourceResultList = rankSaleWeekMapper.getListByWeek(beginWeek, endWeek, size);
        if (Objects.nonNull(curWeekSourceResultList) && !curWeekSourceResultList.isEmpty()) {
            return curWeekSourceResultList;
        }
        return Collections.emptyList();
    }

    /**
     * 获取最新周日期
     *
     * @return
     */
    public String getLastWeek() {
        RankSaleWeekSourceEntity lastWeek = rankSaleWeekMapper.getLastWeek();
        if (Objects.nonNull(lastWeek) && StringUtils.hasLength(lastWeek.getWeek_day())) {
            return lastWeek.getWeek_day();
        }
        return null;
    }

}
