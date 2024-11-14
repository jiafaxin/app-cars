package com.autohome.app.cars.service.components.che168.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SeriesYearCityPriceInfo {

    private int seriesId;

    /**
     * 城市数据
     */
    private PriceRangeInfo cityInfo;

    /**
     * 全国数据
     */
    private PriceRangeInfo all;
}
