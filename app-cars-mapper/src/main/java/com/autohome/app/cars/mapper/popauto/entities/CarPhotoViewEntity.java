package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CarPhotoViewEntity {
    int id;
    int specId;
    int seriesId;
    int picClass;
    String picFilePath;
    int picId;
    int picColorId;
    int isHD;
    int isTitle;
    int specState;
    int syearId;
    int syear;
    int specPicNumber;
    int innerColorId;
    int stateOrder;
    int isClubPhoto;
    BigDecimal classOrder;
    int isclassic;
    int dealerPicOrder;
    int sourceTypeOrder;
    int specPicUploadTimeOrder;
    int width;
    int height;
    int dealerid;
    int pointlocatinid;
    int isWallPaper;
    int optional;
    int showId;
    Date picUploadTime;

    String typename;
    String colorname;
    String specname;
    String showname;
    String seriesName;
    String innerColorName;
}
