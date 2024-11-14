package com.autohome.app.cars.service.components.cms.dtos;

public enum TagStyleBackgroundEnums {
    // Bule("蓝", "#1A206CFE"),
    /** 蓝色-#1A0088FF */
    Bule("蓝", "#1A0088FF"),
    /** 蓝2-#F0F8FF */
    Bule2("蓝2", "#F0F8FF"),
    /** 蓝3-#E9FAFF */
    Bule3("蓝3", "#E9FAFF"),
    Orange("橙", "#1AFF6600"),
    Red("红", "#1AFF4434"),
    RedOrange("热点橙", "#FFFF6600"),
    No("无", "");

    private String text;
    private String color;

    TagStyleBackgroundEnums(String text, String color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}