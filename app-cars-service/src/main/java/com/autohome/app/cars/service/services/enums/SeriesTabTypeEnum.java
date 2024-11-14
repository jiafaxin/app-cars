package com.autohome.app.cars.service.services.enums;

public enum SeriesTabTypeEnum {
    ALL("全部", 0),
    NEWS("资讯", 2),
    KOUBEI("口碑", 10),
    CLUB("论坛", 4),
    SPEC_CLUB("车型论坛", 23),
    BUYCAR("买车", 16),
    USECAR("用车", 15),
    PLAYCAR("玩车", 11),
    SAMELEVEL("同级车", 12),
    MORENEWS("更多动态", 17),
    USEDCAR("买二手车", 18),
    WENDA("问答", 20),
    HOTCOMMENT("热评", 21),
    SPEC_WENDA("车型问答", 22),
    OTHER("其他", -1);

    SeriesTabTypeEnum(String tabName, Integer tabId) {
        this.tabName = tabName;
        this.tabId = tabId;
    }

    private String tabName;
    private Integer tabId;

    public String getTabName() {
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

    public static SeriesTabTypeEnum of(Integer tabId) {
        for (SeriesTabTypeEnum energyTypeEnum : SeriesTabTypeEnum.values()) {
            if (energyTypeEnum.getTabId().intValue() == tabId.intValue()) {
                return energyTypeEnum;
            }
        }
        return SeriesTabTypeEnum.OTHER;
    }
}
