package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ColorInfoEntity implements Serializable {
    int id;
    String name;
    String value;
}
