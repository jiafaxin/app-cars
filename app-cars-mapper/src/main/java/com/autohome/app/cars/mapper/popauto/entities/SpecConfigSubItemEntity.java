package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

@Data
public class SpecConfigSubItemEntity {
    int specId;
    int itemId;
    int subItemId;
    int subValue;
    String logo;
}
