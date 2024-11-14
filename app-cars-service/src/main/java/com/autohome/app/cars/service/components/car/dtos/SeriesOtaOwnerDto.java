package com.autohome.app.cars.service.components.car.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class SeriesOtaOwnerDto {
    private int otaFlag;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date otaPushTime;

    private int otaHistoryCount;

    private int ownerFlag;

    private int allOwnerCount;

    private int owner0Count;

    private int owner1Count;

    private int owner2Count;
    private int owner3Count;

    private int chargeStationFlag;

    private int chargeStationPayType;

    private String chargeStationPayName;

    private String chargeStationName;
}
