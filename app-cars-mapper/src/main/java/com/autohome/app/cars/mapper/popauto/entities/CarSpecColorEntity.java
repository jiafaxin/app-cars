package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class CarSpecColorEntity implements Serializable {
    /**
     * specId
     */
    private int sId;
    //seriesId
    private int ssId;
    /**
     * colorId
     */
    private int cId;
    /**
     * remarks
     */
    private String mark;

    /**
     * price
     */
    private int price;
}
