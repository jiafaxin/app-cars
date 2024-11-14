package com.autohome.app.cars.service.components.hangqing.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dx on 2024/6/20
 */
@Data
public class SeriesSortConditionDto {
    /**
     * 车系id
     */
    private int seriesId;

    /**
     * 关注度值-取自全部关注榜-全国数据
     */
    private int attention;

    /**
     * 月销量数
     */
    private int monthSaleNum;

    /**
     * 品牌id
     */
    private int brandId;

    /**
     * 级别id
     */
    private int levelId;

    /**
     * 是否新能源 0-否 1-是
     */
    private int energytype;

    /**
     * 能源类型
     */
    private String fuelTypes = "";

    /**
     * 车系最低指导价
     */
    private int seriesMinPrice;

    /**
     * 车系最高指导价
     */
    private int seriesMaxPrice;

    /**
     * 所有车型中的指导价最大降幅
     */
    private double maxPriceOff = 0D;


    /**
     * 所有在售车型的报价信息
     */
    private List<SpecCityPriceHis> specPriceHisList = new ArrayList<>();

    @Data
    public static class SpecCityPriceHis {
        /**
         * 车型Id
         */
        private int specId;
        /**
         * 车型历史价格信息
         */
        List<SpecCityPriceHisDto> dtoList;

        // TODO chengjincheng 2024/7/30 测试支持，临时增加
        private double priceOffRecently;
    }

    @Data
    public static class SpecCityPriceHisDto {
        /**
         * 日期
         */
        String date;

        /**
         * 金额
         */
        int newsPrice;
    }


}
