package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

@Data
public class SpecViewEntity {
    int seriesId;
    int specId;
    int minPrice;
    int maxPrice;
    int specPicNum;
    int engineId;
    String engineName;
    int doors;
    String seats;
    int structType;
    String specStructureType;
    Double officalOil;
    int width;
    int length;
    int height;
    int driveForm;
    String specDrivingMode;
    String quality;
    int weightkg;
    int specState;
    int flowMode;
    double specDisplacement;
    int specEnginePower;
    String seriesIsImport;

    int seriesIsImportNum;
    int fuelType;
    double officialFastChargetime;
    double officialSlowChargetime;
    String fastChargeBatteryPercentage;
    double batteryCapacity;
    String endurancemileage;
    String torque;
    String pricedescription;
    String electricMotorGrossPower;
    String electricMotorGrossTorque;
    String engingKW;
    int fuelTypeDetail;
    int syearId;
    int syear;
    String syearName;
    String seat;
    int specOrdercls;
    String specQuality;
    double displacement;
    int specIsImage;
    int isclassic;
    int specIsshow;
    double specOrder;
    int orderBy;
    String specTransmissionType;

    int picNumber;
    int state;
    String seriesPlace;
    String specName;
    String specimg;

    private double ssuo; //specSpeedupOffical
    private int specMaxspeed;
    int appointOrder;
    int specIsPublic;
}
