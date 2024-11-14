package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarPhotoEntity {
    int specId;
    int seriesId;
    int picClass;
    String picFilePath;
    int picId;
    int picColorId;
    int specState;
    int syearId;
    int syear;
    int specPicNumber;
    int innerColorId;
    int isclassic;
    int dealerPicOrder;
    int specPicUploadTimeOrder;
    int width;
    int height;
    int pointlocatinid;
    int isWallPaper;
    int optional;
    int showId;
    int dealerid;

    int stateOrder;
    int isClubPhoto;

    int classOrder;

    int sourceTypeOrder;

    public int getStateOrder() {
        return specState<=30?0:1;
    }

    public int getClassOrder() {
        switch (picClass){
            case 10:
                return 2;
            case 54:
                return 12;
            case 53:
                return 13;
            case 51:
                return 15;
            case 15:
                return 16;
            default:
                return picClass;
        }
    }

    public int getSourceTypeOrder() {
        switch (isClubPhoto){
            case 2:
                return 5;
            case 1:
                return 10;
            case 3:
                return 0;
            default:
                return isClubPhoto;
        }
    }
}
