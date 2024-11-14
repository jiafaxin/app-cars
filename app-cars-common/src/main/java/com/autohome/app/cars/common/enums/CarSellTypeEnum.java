package com.autohome.app.cars.common.enums;

public enum CarSellTypeEnum {
    Selling(1),
    StopSell(2),
    WillSell(3);

    private int value;

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private CarSellTypeEnum(int value) {
        this.value = value;
    }
}
