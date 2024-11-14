package com.autohome.app.cars.service.components.che168.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SeriesYearPriceInfo {

    private int seriesId;

    /**
     * 城市数据
     */
    private List<PriceRangeInfo> cityInfoList = new ArrayList<>();

    /**
     * 全国数据
     */
    private List<PriceRangeInfo> allInfoList = new ArrayList<>();
}
