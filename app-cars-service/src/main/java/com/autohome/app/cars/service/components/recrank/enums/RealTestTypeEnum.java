package com.autohome.app.cars.service.components.recrank.enums;

import lombok.Getter;

/**
 * @author zhangchengtao
 * @date 2024/9/25 9:59
 */
@Getter
public enum RealTestTypeEnum {


    ENDURANCE(105,1209,"续航榜"),
    POWER_CONSUMPTION(106, 1210, "电耗榜");

    private final int rankId;
    private final int typeId;
    private final String rankName;

    RealTestTypeEnum(int rankId, int typeId, String rankName) {
        this.rankId = rankId;
        this.typeId = typeId;
        this.rankName = rankName;
    }


    public static RealTestTypeEnum getByTypeId(int typeId) {
        for (RealTestTypeEnum value : values()) {
            if (typeId == value.typeId) {
                return value;
            }
        }
        return null;
    }

    public static RealTestTypeEnum getByRankId(int rankId) {
        for (RealTestTypeEnum value : values()) {
            if (rankId == value.rankId) {
                return value;
            }
        }
        return null;
    }
}
