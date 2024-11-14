package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class EpibolyAiVideoOrderDetailEntity implements Serializable {

    private int orderId;

    private String sourceId;

    private int status;

    private int pointId;

    private String pointName;
}
