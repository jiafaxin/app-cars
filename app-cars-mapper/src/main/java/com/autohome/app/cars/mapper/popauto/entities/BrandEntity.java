package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.util.Date;

@Data
public class BrandEntity {
    private int id;
    private String name;
    private String fullName;
    private String url;
    private String othern;
    private int ordercls;
    private String synopsis;
    private String years;
    private String country;
    private String firstLetter;
    private String img;
    private String englishName;
    private int countryId;
    private String pngImg;
    private String description;

    private int isNewEnergy;

    private int state;
}
