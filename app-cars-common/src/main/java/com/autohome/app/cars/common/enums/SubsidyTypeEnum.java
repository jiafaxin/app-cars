package com.autohome.app.cars.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author zhangchengtao
 * @date 2024/10/14 17:01
 */
@Getter
public enum SubsidyTypeEnum {
    // 补贴类型ID: 1:新购、2:置换、3:报废买新、-1:全国报废补贴政策
    NEW_BUY(1, "新购", 2),
    REPLACE(2, "置换", 1),
    SCRAP(3, "报废买新", 3),
    ALL(-1, "全国报废补贴政策", 4);
    private final int id;
    private final String name;
    private final int sort;

    SubsidyTypeEnum(int id, String name, int sort) {
        this.id = id;
        this.name = name;
        this.sort = sort;
    }

    public static SubsidyTypeEnum getSubsidyById(int id){
        return Arrays.stream(values()).filter(x->x.id == id).findFirst().orElse(null);
    }
}
