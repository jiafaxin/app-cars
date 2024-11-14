package com.autohome.app.cars.common.enums;

//0:最满意，最不满意，1：最满意。2：最不满意。3：空间。4：动力。5：操控。6：油耗。7：舒适性。
//
//        8：外观。9：内饰。10：性价比
public enum KoubeiTabTypeEnum {
    ALL("全部", 0),
    good("最满意", 1),
    bad("最不满意", 2),
    space("空间", 3),
    power("动力", 4),
    contro("操控", 5),
    oil("油耗", 6),
    comfort("舒适性", 7),
    outlook("外观", 8),
    inner("内饰", 9),
    xjb("性价比", 10);

    KoubeiTabTypeEnum(String tabName, Integer tabId) {
        this.tabName = tabName;
        this.tabId = tabId;
    }

    private String tabName;
    private Integer tabId;

    public String getTabName() {
        return tabName;
    }

    public String getTabName(int isNewEnergy) {
        if (isNewEnergy == 1 && tabId == 6) {
            return "续航";
        }
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public Integer getTabId() {
        return tabId;
    }

    public void setTabId(Integer tabId) {
        this.tabId = tabId;
    }

    public static KoubeiTabTypeEnum of(Integer tabId) {
        for (KoubeiTabTypeEnum energyTypeEnum : KoubeiTabTypeEnum.values()) {
            if (energyTypeEnum.getTabId().intValue() == tabId.intValue()) {
                return energyTypeEnum;
            }
        }
        return KoubeiTabTypeEnum.ALL;
    }
}
