package com.autohome.app.cars.apiclient.che168.dtos;

import lombok.Data;

@Data
public class GetCarSpecPirceRangeResult {

    private int specid;
    private String minprice;
    private String specurl;
    private String seriesurl;
    private String name;
    private String maxprice;
}
