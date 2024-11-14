package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConfigItemBaseInfo implements Serializable {
    int itemId;
    String itemName;
    int dynamicShow;
    int cVIsShow;
    int isShow;
    int displayType;
    String logo;
}
