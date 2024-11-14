package com.autohome.app.cars.service.components.subsidy.enums;


/**
 * @author chengjincheng
 * @date 2024/9/2
 */
public enum SubsidyPolicyEntryItemTypeEnum {

    BRAND("品牌补贴", 1),
    CITY("城市政策补贴", 2),
    OTHER("其他补贴", 3),
    BRAND_NEW("品牌新购补贴", 4),
    BRAND_REPLACE("品牌置换补贴", 5),
    BRAND_ADD("品牌增购补贴", 6),
    CITY_NEW("城市新购补贴", 7),
    CITY_REPLACE("城市置换补贴", 8),
    CITY_ADD("城市增购补贴", 9),
    ;

    private String type;
    private int value;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    SubsidyPolicyEntryItemTypeEnum(String type, int value) {
        this.type = type;
        this.value = value;
    }
}
