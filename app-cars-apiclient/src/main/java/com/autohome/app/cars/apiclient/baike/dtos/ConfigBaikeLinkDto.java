package com.autohome.app.cars.apiclient.baike.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigBaikeLinkDto {
    private List<ConfigBaikeLinkDto> sublist = new ArrayList<>();
    private Integer id;
    private String name;
    private String link;
    private String mlink;
    private String firstpartcnt;
    private String mid;
    private String repImg;
    private String videocover;
}
