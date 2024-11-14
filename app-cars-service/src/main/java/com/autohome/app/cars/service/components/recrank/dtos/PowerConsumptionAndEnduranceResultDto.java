package com.autohome.app.cars.service.components.recrank.dtos;

import com.autohome.app.cars.service.components.recrank.dtos.configdtos.BaseEnergyCountDataDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/9/25 19:20
 */
@Data
public class PowerConsumptionAndEnduranceResultDto {
    private List<RankDataDto> dataList = new ArrayList<>();

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
         * 实测车型ID
         */
        private Integer testSpecId;

        /**
         * 实测车型名称
         */
        private String testSpecName;


        private String showValue;

        private String unit;

        /**
         * 本月排名 销量相同会重复
         */
        private int rn;
        /**
         * 本月排名 销量相同不会重复
         */
        private int rnNum;
    }
}
