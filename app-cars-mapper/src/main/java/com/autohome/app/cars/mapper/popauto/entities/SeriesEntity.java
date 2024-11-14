package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class SeriesEntity {
    private int id;
    private String name;
    private int levelId;
    private String levelName;
    private String img;
    private String nobgImg;
    private int isNewEnergy;
    private Date createTime;
    private int isPublic;
    private String isImport;
    private String place;
    private int state;
    private String firstLetter;
    private int delegate25SpecId;
    private int newenergySeriesId;
    private int diffConfigIsShow;
    private String priceDescription;
    private int seriesNewRank;
    private int seriesPriceMin;
    private int seriesPriceMax;
    private int brandId;
    private String brandLogo;
    private String brandName;
    private int manufactoryId;
    private String manufactoryName;

}
