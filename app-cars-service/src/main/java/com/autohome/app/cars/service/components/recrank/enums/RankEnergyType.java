package com.autohome.app.cars.service.components.recrank.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RankEnergyType {

    OFV(1, "燃油车"),
    EV(4, "纯电动"),
    PHEV(5, "插电混合"),

    REEV(6, "增程式");
    private final int type;
    private final String name;


    public static String getNameByType(int type) {
        for (RankEnergyType value : RankEnergyType.values()) {
            if (value.getType() == type) {
                return value.getName();
            }
        }
        return null;
    }
}
