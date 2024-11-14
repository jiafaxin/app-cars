package com.autohome.app.cars.service.components.subsidy.enums;

/**
 * @author chengjincheng
 * @date 2024/5/29
 */
public enum SubsidyGroupTypeEnum {

    // 厂商补贴-置换
    FACTORY_BENEFITS_REPLACE(1),

    // 厂商补贴-增购
    FACTORY_BENEFITS_ADD(2),

    // 厂商补贴-新购
    FACTORY_BENEFITS_NEW(3),

    // 地方补贴-置换
    LOCAL_BENEFITS_REPLACE(4),

    // 地方补贴-新购
    LOCAL_BENEFITS_NEW(5),
    ;

    private int type;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    SubsidyGroupTypeEnum(int type) {
        this.type = type;
    }
}
