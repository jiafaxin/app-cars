package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 新能源榜-电耗榜&续航榜 数据DTO
 */
@Getter
@Setter
public class NewEnergyPowerConsumptionAndBatteryLifeResultDto {
    private List<RankDataDto> dataList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RankDataDto {
        /**
         * 车系Id
         */
        private Integer seriesId;

        /**
         * 热门车型ID
         */
        private Integer hostSpecId;
        /**
         * 车系级别
         */
        private String levelId;
        /**
         * 车系图片
         */
        private String seriesImage;
        /**
         * 车系名称
         */
        private String seriesName;

        /**
         * 实测车型ID
         */
        private Integer specId;

        /**
         * 实测车型名称
         */
        private String specName;

        /**
         * 能耗
         */
        private double powerConsumption;

        /**
         * 续航里程
         */
        private double endurance;

        /**
         * 车系最低价格
         */
        private int minPrice;

        /**
         * 车系最高价格
         */
        private int maxPrice;

        /**
         * 车系品牌ID
         */
        private Integer brandId;


        /**
         * 车系销售状态
         */
        private Integer state;

        /**
         * 新能源类型
         */
        private int energyType;
        /**
         * 厂商类型
         */
        private String manuType;


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
