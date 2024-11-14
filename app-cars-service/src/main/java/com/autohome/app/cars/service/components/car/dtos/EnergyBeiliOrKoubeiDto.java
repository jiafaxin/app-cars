package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyBeiliOrKoubeiDto {
    /**
     * 真实续航：xx KM
     */
    String factEnergy100;
    /**
     * 百公里电耗：xx 元
     */
    String factDriveRange;
}
