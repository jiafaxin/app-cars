package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecColorListItemsDto;
import lombok.Data;

import java.util.List;

@Data
public class SpecColorDto {

    private int specid;
    private List<SpecColorListItemsDto.ColorItem> coloritems;

}
