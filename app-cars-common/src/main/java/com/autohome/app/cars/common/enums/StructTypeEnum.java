package com.autohome.app.cars.common.enums;

/**
 * 车身结构类型
 */
public enum StructTypeEnum {
    car_lxc("两厢车", 1),
    car_sxc("三厢车", 2),
    car_xbc("掀背车", 3),
    car_lvxc("旅行车", 4),
    car_ydcpc("硬顶敞篷车", 5),
    car_rdcpc("软顶敞篷车", 6),
    car_ydpc("硬顶跑车", 7),
    car_kec("客车", 8),
    car_huoc("货车", 9),
    car_pika("皮卡", 10),
    car_mpv("MPV", 11),
    car_suv("SUV", 12),
    car_sxkjc("三厢跨界车", 13),
    car_lxkjc("两厢跨界车", 14),
    car_lvxkjc("旅行跨界车", 15),
    car_suvkjc("SUV跨界车", 16),
    car_kjc("跨界车", 1000);
    private int value;
    private String type;

    public static String getTypeByValue(int value) {
        StructTypeEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            StructTypeEnum type = var1[var3];
            if (type.value == value) {
                return type.type;
            }
        }

        return "";
    }

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

    private StructTypeEnum(String type, int value) {
        this.type = type;
        this.value = value;
    }
}
