package com.autohome.app.cars.apiclient.car.dtos;

import lombok.Data;

@Data
public class ConfigItemResult {

    /**
     * id : 1
     * name : 主/副驾驶座安全气囊
     * typeid : 1
     * disptype : 0
     * isshow : 1
     * cvisshow : 1
     * itemorder : 10
     * typeorder : 10
     */

    private int id;
    private String name;
    private int typeid;
    private int disptype;
    private int isshow;
    private int cvisshow;
    private int itemorder;
    private int typeorder;

}
