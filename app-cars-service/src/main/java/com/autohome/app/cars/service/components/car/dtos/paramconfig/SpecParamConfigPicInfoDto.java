package com.autohome.app.cars.service.components.car.dtos.paramconfig;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpecParamConfigPicInfoDto {

    private int specId;
    private List<SpecParamConfigPicTipDto> list = new ArrayList<>();

}
