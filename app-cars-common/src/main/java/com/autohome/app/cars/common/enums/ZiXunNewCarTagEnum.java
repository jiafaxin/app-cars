package com.autohome.app.cars.common.enums;

/**
 * 1-全新车系，2-车系换代，3-车系改款，4-新加车型，5-中期改款，6-小改款
 *
 * @author chengjincheng
 * @date 2024/3/28
 */
public enum ZiXunNewCarTagEnum {

    QuanXinCheXi("全新车系", 1),

    CheXiHuanDai("车系换代", 2),

    CheXiGaiKuan("车系改款", 3),

    XinJiaCheXing("新加车型", 4),

    ZhongQiGaiKuan("中期改款", 5),

    XiaoGaiKuan("小改款", 6),
    ;

    private int value;
    private String type;

    public static String getTypeByValue(int value) {
        ZiXunNewCarTagEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ZiXunNewCarTagEnum type = var1[var3];
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

    private ZiXunNewCarTagEnum(String type, int value) {
        this.type = type;
        this.value = value;
    }
}
