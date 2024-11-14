package com.autohome.app.cars.apiclient.recommend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class RecommendPkParam {
    private String device_id;
    private List<SeriesSpecPair> series;
    private Integer net_state;
    private String device_type;
    private Integer source;
    private String rid;
    private String version;
    private Integer city_id;
    private String uid;
    private String exp;
    private String baseline;
    private String baselineversion;
    private Boolean is_debug;
}
