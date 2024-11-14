package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestDataSpecBasePo implements Serializable {

    private int dataId;

    private int fuelTypeDetail;

    private int brandId;

    private int seriesId;

    private int specId;

    private String seriesName;

    private int seriesMinPrice;

    private int seriesMaxPrice;

    private int seriesLevelId;

    private String firstLetter;

    private int specPrice;

    private String specName;

    private int itemId;

    private String itemName;

    private String unit;

    private String showValue;

    private String newValue;
    private int level1ItemId;

    private String level1ItemName;

    private int level2ItemId;

    private String level2ItemName;

    private int level3ItemId;

    private String level3ItemName;
}
