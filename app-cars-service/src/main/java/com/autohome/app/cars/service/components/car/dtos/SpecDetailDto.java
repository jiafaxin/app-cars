package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author chengjincheng
 * @date 2024/2/29
 */
@Data
public class SpecDetailDto {

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
     * 燃烧类型(即原表中的fueltypedetail字段)
     */
    private int fuelType;

    /**
     * "纯电","插电","氢燃料","增程式"
     */
    private String energyTypeName;

    /**
     * 最低指导价；乘用车的这两价格一样
     */
    private int minPrice;

    /**
     * 最高指导价；乘用车的这两价格一样
     */
    private int maxPrice;

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
     * 是不是在前台显示参数
     */
    private int paramIsShow;

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
     * 0未售，10待售，20在售，30停产在售，40停产
     */
    private int state;

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
    private boolean taxExemption;

    /**
     * 发动机排量
     */
    private BigDecimal displacement;

    /**
     * 座位数,像商用车：5-6、5/7/8 这样的数据
     */
    private String seats;
    /**
     * 座位数
     */
    private int seatCount;
    /**
     * 年代款，2024
     */
    private int yearName;
    /**
     * 年代款id
     */
    private int yearId;
    /**
     * 进气形式id
     */
    private int flowModeId;

    /**
     * 根据进气方式id获取进气方式名称
     */
    public String getFlowModeName() {
        String strResult = StringUtils.EMPTY;
        switch (flowModeId) {
            case 1:
                strResult = "自然吸气";
                break;
            case 2:
                strResult = "涡轮增压";
                break;
            case 3:
                strResult = "机械增压";
                break;
            case 4:
                strResult = "机械+涡轮增压";
                break;
            case 5:
                strResult = "双涡轮增压";
                break;
            case 6:
                strResult = "三涡轮增压";
                break;
            case 7:
                strResult = "四涡轮增压";
                break;
            case 8:
                strResult = "双机械增压";
                break;
        }
        return strResult;
    }

    /**
     * 电动机总功率
     */

    private double electricKw;

    /**
     * 电动车续航里程（官方续航）
     */
    private int enduranceMileage;

    /**
     * 马力
     */
    private int enginePower;
    /**
     * 是否是海外车型
     */
    private boolean isForeignCar;
    /**
     * 是否为图片车型
     */
    private boolean isImageSpec;
    /**
     * 是否经典车型
     */
    private boolean isClassic;
    /**
     * 变速箱名称
     */
    private String transmission;
    /**
     * 驱动类型名称
     */
    private String drivingModeName;
    /**
     * 环保标准
     */
    private String emissionStandards;
    /**
     * 一个用于排序的值
     */
    private int orders;
    /**
     * 是否是新车
     */
    private boolean isnewcar;
    /**
     * 车身结构
     */
    private String structtype;
    /**
     * 变速箱类型
     */
    private String gearbox;
}
