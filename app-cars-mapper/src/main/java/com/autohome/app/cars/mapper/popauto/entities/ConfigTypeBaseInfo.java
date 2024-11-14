package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ConfigTypeBaseInfo implements Serializable {
    int typeId;
    String typeName;
    List<ConfigItemBaseInfo> items;
}
