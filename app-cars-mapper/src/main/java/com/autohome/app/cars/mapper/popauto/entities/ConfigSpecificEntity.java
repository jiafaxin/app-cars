package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfigSpecificEntity {

    private int itemId;

    private String itemName;

    private String baiKeUrl;

    private int baiKeId;

    private int specId;

    private int itemValue;

    private BigDecimal price;

    private int sort;

}
