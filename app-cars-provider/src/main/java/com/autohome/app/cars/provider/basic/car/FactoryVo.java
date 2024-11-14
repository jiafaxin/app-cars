package com.autohome.app.cars.provider.basic.car;

import lombok.Data;

@Data
public class FactoryVo {
    private int id;

    private String py;

    private String name;

    private String place;

    /**
     * 车系状态
     */
    private int stateSort;

    public int getPlaceSort() {
        int sort = 0;
        place = place == null ? "" : place;
        switch (place) {
            case "国产":
                sort = 1;
                break;
            case "合资":
                sort = 2;
                break;
            case "独资":
                sort = 3;
                break;
            case "进口":
                sort = 4;
                break;
        }
        return sort;
    }
}
