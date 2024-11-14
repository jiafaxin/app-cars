package com.autohome.app.cars.service.components.recrank.dtos;

import com.autohome.app.cars.service.components.recrank.dtos.configdtos.BaseEnergyCountDataDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/4/29 9:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthRankDataResultDto {
    private List<RankDataDto> dataList;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RankDataDto extends BaseEnergyCountDataDto {
        /**
         * 车系Id
         */
        private Integer seriesId;

        /**
         * 热门车型ID
         */
        private Integer hostSpecId;


        /**
         * 车系口碑分
         */
        private String scoreValue;
        /**
         * 车系最低价格
         */
        private int minPrice;

        /**
         * 车系最高价格
         */
        private int maxPrice;

        /**
         * 新能源类型
         */
        private int energyType;

        /**
         * 销量月份
         */
        private String month;

        /**
         * 能源类型
         */
        private String fuelTypes;
        /**
         * 本月排名 销量相同会重复
         */
        private int rn;
        /**
         * 本月排名 销量相同不会重复
         */
        private int rnNum;
        /**
         * 上月排名
         */
        private int preRankNum;
    }
}
