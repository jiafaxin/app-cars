package com.autohome.app.cars.common.carconfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class Spec {
    public static boolean isCvSpec(int specId) {
        return specId > 1000000;
    }


    public static Integer getFueltypeDetailId(String fueltypeDetailName) {
        Integer result = 0;
        switch (fueltypeDetailName) {
            case "汽油":
                result = 1;
                break;
            case "柴油":
                result = 2;
                break;
            case "油电混合":
                result = 3;
                break;
            case "纯电动":
                result = 4;
                break;
            case "插电式混合动力":
                result = 5;
                break;
            case "增程式":
                result = 6;
                break;
            case "氢燃料":
                result = 7;
                break;
            case "汽油+48V轻混系统":
                result = 8;
                break;
            case "汽油+24V轻混系统":
                result = 9;
                break;
            case "汽油+90V轻混系统":
                result = 10;
                break;
            case "汽油+CNG":
            case "汽油+天然气":
                result = 11;
                break;
            case "汽油电驱":
                result = 12;
                break;
            case "柴油+48V轻混系统":
                result = 13;
                break;
            case "CNG":
                result = 14;
                break;
            case "甲醇混动":
                result = 15;
                break;
            case "氢燃料电池":
                result = 16;
                break;
        }
        return result;
    }

    public static Map<String, String> DicConfig_Group = new LinkedHashMap<String, String>() {{
        put("被动安全", "安全配置");
        put("主动安全", "安全配置");
        put("驾驶操控", "操控配置");
        put("四驱/越野", "操控配置");
        put("驾驶硬件", "智能/辅助驾驶");
        put("驾驶功能", "智能/辅助驾驶");
        put("车外灯光", "外部配置");
        put("外观/防盗", "外部配置");
        put("天窗/玻璃", "外部配置");
        put("外后视镜", "外部配置");
        put("屏幕/系统", "内部配置");
        put("智能化配置", "内部配置");
        put("方向盘/内后视镜", "内部配置");
        put("车内充电", "内部配置");
        put("座椅配置", "内部配置");
        put("音响/车内灯光", "内部配置");
        put("空调/冰箱", "内部配置");
    }};
}
