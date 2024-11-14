package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConfigBaseEntity implements Serializable {

    private int typeId;

    private int id;

    private String name;

    private int displayType;

    private int isShow;

    private int cvIsShow;

    private int itemOrder;

    private int typeOrder;
}
