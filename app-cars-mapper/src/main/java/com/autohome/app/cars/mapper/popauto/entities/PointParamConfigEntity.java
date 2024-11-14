package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class PointParamConfigEntity implements Serializable {

    private int buId;

    private int pointLocationId;

    private int dataType;

    private int paramConfigId;

    private String paramConfigName;


}
