package com.autohome.app.cars.apiclient.recommend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class PkResultInfoDto {

    private List<SeriesSpecPair> data;

    private String deviceId;

    private String pvid;

    private String rid;

    private String pvdata;
}
