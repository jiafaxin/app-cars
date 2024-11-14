package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

@Data
public class ConfigItemEntity {
    int typeId;
    String typeName;
    int itemId;
    String itemName;
    int dynamicShow;
    int cVIsShow;
    int isShow;
    int displayType;
    String logo;
}
