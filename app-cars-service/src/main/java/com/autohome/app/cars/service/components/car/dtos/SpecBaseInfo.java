package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chengjincheng
 * @date 2024/2/29
 */
@Data
public class SpecBaseInfo {

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
     * 1电车，0非电车
     */
    private int energyType;

    /**
     * "纯电","插电","氢燃料","增程式"
     */
    private String energyTypeName;

    /**
     * 厂商指导价
     */
    private String fctPrice;

    /**
     * 厂商指导价标题
     */
    private String fctPriceName;

    /**
     * 级别id
     */
    private int levelId;

    /**
     * 级别名称
     */
    private String levelName;

    /**
     * 车型的logo
     */
    private String logo;

    /**
     * logo类型id，埋点使用，目前写死101051
     */
    private int logoTypeId;


    /**
     * 是不是在前台显示参数
     */
    private int paramIsShow;

    /**
     * 图片数量
     */
    private int picCount;

    /**
     * 是否在售
     */
    private int salestate = -1;// 是否在售（老字段：specstate）1-在售，0-非在售;默认不能为0;

    /**
     * 车系id
     */
    private int seriesId;

    /**
     * 车系名称
     */
    private String seriesName;

    /**
     * 车型Id
     */
    private int specId;

    /**
     * 车型名称
     */
    private String specName;

    /**
     * 定制车
     */
    private String specNameLink;

    /**
     * 0未售，10待售，20在售，30停产在售，40停产
     */
    private int state;

    private int tabdefaluttypeid;

    /**
     * 厂商指导价说明
     */
    private String fctPriceTipInfo;


    /**
     * 厂商Id
     */
    private int manufactoryId;

    /**
     * 厂商名称
     */
    private String manufactoryName;

    /**
     * 年代款
     */
    private int yearId;

    /**
     * 年代
     */
    private String yearName;

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
     * 车型指导价格说明
     */
    private String priceDescription;

    /**
     * 是否接受预定：0否、1是
     */
    private boolean isBooked;

    /**
     * 是否免税
     */
    private int isTaxExemption;


}
