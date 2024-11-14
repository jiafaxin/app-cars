package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpecEntity {

    private int id;

    private String name;

    private int seriesId;

    private String seriesName;

    /**
     * 品牌Id
     */
    private int brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌logo
     */
    private String brandLogo;

    /**
     * 厂商Id
     */
    private int manufactoryId;

    /**
     * 厂商名称
     */
    private String manufactoryName;

    /**
     * 年代款id
     */
    private int yearId;

    /**
     * 年代款，2024
     */
    private int yearName;

    /**
     * 0未售，10待售，20在售，30停产在售，40停产
     */
    private int state;

    /**
     * 车型的logo
     */
    private String img;

    private int picNumber;

    /**
     * 最低指导价；乘用车的这两价格一样
     */
    private int minPrice;

    /**
     * 最高指导价；乘用车的这两价格一样
     */
    private int maxPrice;

    /**
     * 上市时间，编辑维护
     */
    private String timeMarket;
    /**
     * 车型创建时间
     */
    private String dtime;

    /**
     * 是不是在前台显示参数
     */
    private int paramIsShow;

    /**
     * 是不是在前台显示参数（根据在售状态和isshow决定）
     */
    private int paramIsShowByState;

    /**
     * 发动机排量
     */
    private BigDecimal displacement;

    /**
     * 进气方式:1,自然吸气;2,涡轮增压;3,机械增压;4,机械+涡轮增压
     */
    private int flowMode;

    /**
     * 燃烧类型(即原表中的fueltypedetail字段)
     */
    private int fuelType;

    /**
     * 级别id
     */
    private int levelId;

    /**
     * 车型指导价格说明
     */
    private String priceDescription;

    /**
     * 座位数,像商用车：5-6、5/7/8 这样的数据
     */
    private String seats;

    /**
     * 是否接受预定：0否、1是
     */
    private boolean isBooked;

    /**
     *
     */
    private Integer specTaxType;

    /**
     * 电动机功率
     */
    private String electricMotorGrossPower;
    /**
     * 电动车续航里程（官方续航）
     */
    private int enduranceMileage;
    /**
     * 马力
     */
    private Integer specEnginePower;
    /**
     * 是否是海外车型
     */
    private Integer isForeignCar;

    private Integer IsImageSpec;
    private Integer isclassic;
    private Integer Orders;
}
