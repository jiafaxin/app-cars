package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

@Data
public class CarLevelEntity {

    private int id;
    private String name;
    private String dir;
    private String description;

    public String getName() {
        if (name.contains("皮卡"))
            return "皮卡";
        return name;
    }
}
