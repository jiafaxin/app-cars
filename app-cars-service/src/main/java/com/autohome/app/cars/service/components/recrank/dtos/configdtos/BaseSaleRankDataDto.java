package com.autohome.app.cars.service.components.recrank.dtos.configdtos;

import lombok.Data;

@Data
public class BaseSaleRankDataDto {

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
     * 存在销量的能源类型
     */
    private String energyTypes = "";

    /**
     * 销量月份
     */
    private String weekDay;

    /**
     * 能源类型
     */
    private String fuelTypes;

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
     * 车系品牌ID
     */
    private Integer brandId;

    /**
     * 车系销量
     */
    private long saleCount;

    /**
     * 厂商类型
     */
    private String manuType;

    /**
     * 在售状态
     */
    private int state;

    /**
     * 排名变化
     */
    private int rankChange;

}
