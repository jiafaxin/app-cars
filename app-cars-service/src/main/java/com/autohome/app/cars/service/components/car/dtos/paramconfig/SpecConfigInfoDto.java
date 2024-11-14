package com.autohome.app.cars.service.components.car.dtos.paramconfig;

import com.autohome.app.cars.apiclient.car.dtos.SpecConfigResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpecConfigInfoDto {

    private int specId;
    private List<SpecConfigResult.Configtypeitems> configtypeitems  = new ArrayList<>();

}
