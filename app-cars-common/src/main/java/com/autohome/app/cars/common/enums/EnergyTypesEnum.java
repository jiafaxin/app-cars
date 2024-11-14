package com.autohome.app.cars.common.enums;


public enum EnergyTypesEnum {
    fuel("燃油车", 1),
    pure_elect("纯电动", 4),
    cha_dian("插电式混合动力", 5),
    zeng_cheng("增程式", 6),
    H2_energy("氢燃料", 7);

    private int value;
    private String type;

    public static String getTypeByValue(int value) {
        EnergyTypesEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EnergyTypesEnum type = var1[var3];
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

    private EnergyTypesEnum(String type, int value) {
        this.type = type;
        this.value = value;
    }
}
