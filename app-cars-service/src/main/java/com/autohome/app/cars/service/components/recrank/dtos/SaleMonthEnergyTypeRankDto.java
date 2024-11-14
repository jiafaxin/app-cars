package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by dx on 2024/7/9
 */
@NoArgsConstructor
@Data
public class SaleMonthEnergyTypeRankDto {
    private String month;
    private List<SaleMonthEnergyTypeRankListDto> list;
}
