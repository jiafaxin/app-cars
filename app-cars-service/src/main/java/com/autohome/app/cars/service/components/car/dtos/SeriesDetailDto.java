package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.common.utils.PriceUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class SeriesDetailDto {
    /**
     * 车系id
     */
    private int id;
    /**
     * 车系名称
     */
    private String name;
    /**
     * 车系代表图
     */
    private String logo;

    /**
     * 车系透明图
     */
    private String pngLogo;
    /**
     * 车系最低价
     */
    private int minPrice;
    /**
     * 最低价对应的车型id
     */
    private int minPriceSpecId;
    /**
     * 车系最高价
     */
    private int maxPrice;
    /**
     * 车系状态
     */
    private int state;
    /**
     * 是否包含预定车型
     */
    private int containBookedSpec;
    /**
     * 车系排名
     */
    private int rank;
    /**
     * 品牌id
     */
    private int brandId;
    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 特殊符号，解码后的，比如：车系id:5153
     * Pi&#235;ch Automotive
     * 编码后的
     * Piëch Automotive
     */
    private String brandCodeName;
    /**
     * 品牌logo
     */
    private String brandLogo;
    /**
     * 厂商id
     */
    private int fctId;
    /**
     * 厂商名称
     */
    private String fctName;
    /**
     * 加了编码解码后的
     */
    private String fctCodeName;
    /**
     * 车系级别
     */
    private int levelId;
    /**
     * 车系级别名称
     */
    private String levelName;
    /**
     * 车系产地
     */
    private String place;

    int energytype;

    String fueltypes = "";

    /**
     * 热门车型id
     */
    int hotSpecId;

    /**
     * 热门车型名
     */
    String hotSpecName;

    /**
     * 参数是否外显
     */
    int paramIsShow;

    /**
     * 发动机排量
     */
    List<String> displacementItems;

    /**
     * 在售车型数
     */
    int sellSpecNum;

    int stopSpecNum;

    int waitSpecNum;
    /**
     * 智能驾驶xx项功能
     */
    int intelligentDrivingNum;
    /**
     * 车系上市时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "Asia/Shanghai")
    Date onLineTime;
    /**
     * 资讯新车上市标签：
     * 全新车系 1
     * 车系换代 2
     * 中期改款 5
     * 小改款 6
     * 新加车型 4
     */
    int newBrandTagId;
    /**
     * 价格描述
     */
    private String priceDescription;

    int liveStatus;

    public int getRank() {
        if (rank == 0) {
            return Integer.MAX_VALUE;
        }
        return rank;
    }

    public String getNewenergypricetip() {
        if (getFctId() == 92 && getId() == 4691) {
            return "郑州日产生产";
        }
        return "";
    }

    public String getPrice() {
        if (containBookedSpec > 0) {
            return "接受预订";
        }
        return PriceUtil.GetPriceStringDetail(getMinPrice(), getMaxPrice(), getState());
    }

    public int getMiniprice() {
        String price = getPrice();
        if (price.equals("暂无报价") || price.equals("即将销售") || price.equals("接受预订")) {
            return 999999999;
        }
        return getMinPrice();
    }
    boolean isNewCar;
    /**
     * 是否是新车车系
     */
    public boolean getIsNewCar(){
        return isNewCar;
    }
}
