package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.service.components.subsidy.dtos.CityLocalSubsidyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubInfoDto {
    private int index = -1;
    private CityLocalSubsidyDto subsidyDto;
}