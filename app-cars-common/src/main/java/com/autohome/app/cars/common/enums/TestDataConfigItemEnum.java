package com.autohome.app.cars.common.enums;

/**
 * 实测数据 参数 配置项 拼接模板枚举
 *
 */
public enum TestDataConfigItemEnum {

    ChengZuoKongJian("实测乘坐空间", 10001),

    YueYeNengLi("实测越野能力", 10002),

    ShaCheJuLi("刹车距离", 10003),

    ZongHeYouHao("综合油耗", 10004),

    ZongHeXuHang("综合续航", 10005),

    ZhuDongAnQuan("实测主动安全", 10006),

    ZhiNengJiaShi("实测智能驾驶", 10007),

    ZhiNengZuoCang("实测智能座舱", 10008)
    ;


    private int value;
    private String name;

    public static String getNameByValue(int value) {
        TestDataConfigItemEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            TestDataConfigItemEnum type = var1[var3];
            if (type.value == value) {
                return type.name;
            }
        }
        return "";
    }

    public static int getValueByName(String name) {
        TestDataConfigItemEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            TestDataConfigItemEnum type = var1[var3];
            if (type.name.equals(name)) {
                return type.value;
            }
        }
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private TestDataConfigItemEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
