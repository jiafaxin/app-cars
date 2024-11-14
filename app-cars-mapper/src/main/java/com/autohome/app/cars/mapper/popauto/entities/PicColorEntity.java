package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class PicColorEntity implements Serializable {
    int seriesId;
    int specId;
    int syearId;
    int syear;
    int specState;
    int colorId;
    String colorName;
    String colorValue;
    int picClass;
    int picNumber;
    int clubPicNumber;
    int classOrder;



    public int getClassOrder() {
        switch (picClass){
            case 10:
                return 2;
            case 54:
                return 13;
            case 53:
                return 14;
            case 51:
                return  15;
            case 15:
                return 16;
            default:
                return  picClass;
        }
    }


}
