package com.autohome.app.cars.service.components.cms.dtos;

public enum TagStyleFontColorEnums {
    // Bule("蓝色", "#FF206CFE"),
    /** 蓝色-#0088FF */
    Bule("蓝色", "#0088FF"),
    Orange("橙", "#FFFF6600"),
    Red("红", "#FFFF4434"),
    // Black("黑", "#FF111E36"),
    Black("黑", "#464E64"),
    White("白", "#FFFFFFFF"),
    No("无", "");

    private String text;
    private String color;

    TagStyleFontColorEnums(String text, String color) {
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