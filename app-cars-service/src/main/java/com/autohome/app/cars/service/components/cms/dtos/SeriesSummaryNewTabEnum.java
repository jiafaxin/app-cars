package com.autohome.app.cars.service.components.cms.dtos;


import lombok.Data;

public enum SeriesSummaryNewTabEnum {


    /**
     * 全部
     */
    ALL("全部",0,"recommend"),
    AUTOSHOW("车展",10007,"autoshow"),
    /**
     * 视频
     */
    VIDEO("视频",10005,"video"),

    /**
     * 精选
     */
    ORIGINAL("原创",10003,"original"),

    /**
     * 评测
     */
    EVALUATION("评测",10002,"review"),
;

    private int value;

    private String infoType;
    private String name;

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getInfoType() {
        return infoType;
    }

    SeriesSummaryNewTabEnum(int value) {
        this.value = value;
    }

    SeriesSummaryNewTabEnum(String name, int value, String infoType) {
        this.name = name;
        this.value = value;
        this.infoType = infoType;
    }

    public static SeriesSummaryNewTabEnum getByValue(int value){
        for (SeriesSummaryNewTabEnum seriesSummaryNewTabEnum : SeriesSummaryNewTabEnum.values()) {
            if (seriesSummaryNewTabEnum.value == value) {
                return seriesSummaryNewTabEnum;
            }
        }
        return null;
    }
}
