package com.autohome.app.cars.common.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 高德对接榜单类型
 * @author zhangchengtao
 * @date 2024/9/14 13:58
 */
@Getter
public enum RankLanTypeEnum {
    MONTH_RANK(1, "销量榜"),
    MONTH_BRAND_RANK(2, "品牌榜"),
    CITY_RANK(3, "城市榜"),
    NEW_ENERGY_MONTH_RANK(4, "新能源-销量月榜"),
    NEW_ENERGY_MONTH_BRAND_RANK(5, "新能源-品牌月榜"),
    KEEP_VALUE_RANK(6, "保值榜");

    RankLanTypeEnum(int typeId, String name) {
        this.typeId = typeId;
        this.name = name;
    }


    private final int typeId;
    private final String name;


    public static RankLanTypeEnum getByTypeId(int typeId) {
        for (RankLanTypeEnum type : RankLanTypeEnum.values()) {
            if (type.typeId == typeId) {
                return type;
            }
        }
        return null;
    }
}
