package com.autohome.app.cars.service.components.subsidy.enums;

import lombok.Getter;

/**
 * @author : zzli
 * @description : 补贴类型
 * @date : 2024/10/15 19:18
 */
@Getter
public enum PolicyTypeEnum {
    NATIONAL_SCRAP_SUBSIDY("全国报废补贴政策", "全国报废补贴", 10),

    SCRAP_BUY_FUEL_CAR("报废买燃油车政策", "买燃油车报废补贴", 20),

    SCRAP_BUY_NEW_ENERGY_CAR("报废买新能源车政策", "买新能源报废补贴", 30),

    SCRAP_BUY_NEW_CAR("报废买新车政策", "买新车报废补贴", 40),

    NEW_BUY_NEW_CAR("新购新车政策", "购新车补贴", 50),

    NEW_BUY_CAR("新购车政策", "购新车补贴", 50),

    NEW_BUY_FUEL_CAR("新购燃油车政策", "燃油新购补贴", 60),

    NEW_BUY_NEW_ENERGY_CAR("新购新能源车政策", "新能源新购补贴", 70),

    EXCHANGE_BUY_PURE_ELECTRIC_CAR("置换买纯电动车政策", "纯电动置换补贴", 80),

    EXCHANGE_BUY_NEW_ENERGY_CAR("置换买新能源车政策", "新能源置换补贴", 90),

    EXCHANGE_BUY_FUEL_CAR("置换买燃油车政策", "燃油车置换补贴", 100),

    EXCHANGE_BUY_NEW_CAR("置换买新车政策", "新车置换补贴", 110);

    private final String title;
    private final String alias;
    private final int id;

    PolicyTypeEnum(String title, String alias, int id) {
        this.title = title;
        this.alias = alias;
        this.id = id;
    }

    public static PolicyTypeEnum getByType(String title) {
        for (PolicyTypeEnum value : values()) {
            if (value.getTitle().equals(title)) {
                return value;
            }
        }
        return null;
    }
}
