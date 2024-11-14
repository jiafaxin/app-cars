package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by dx on 2024/7/9
 */
@NoArgsConstructor
@Data
public class SaleMonthEnergyTypeRankListDto {
    private int brandid;
    private int serieId;
    private long allCount;
    private long fuelCount;
    private long energyCount;
    private long energy4Count;
    private long energy5Count;
    private long energy6Count;
}
