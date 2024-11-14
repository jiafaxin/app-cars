package com.autohome.app.cars.common.enums;

/**
 * 新能源燃料类型
 */
public enum EnergyTypesNewEnum {
    pure_elect("纯电", 4),
    cha_dian("插电", 5),
    H2_energy("氢燃料", 7),
    zeng_cheng("增程式", 6);

    private int value;
    private String type;

    public static String getTypeByValue(int value) {
        EnergyTypesNewEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            EnergyTypesNewEnum type = var1[var3];
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

    private EnergyTypesNewEnum(String type, int value) {
        this.type = type;
        this.value = value;
    }
}
