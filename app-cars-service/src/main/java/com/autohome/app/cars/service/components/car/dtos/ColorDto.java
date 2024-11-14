package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.mapper.popauto.entities.PicColorEntity;
import lombok.Data;

import java.util.List;

@Data
public class ColorDto {
    int seriesId;
    List<PicColorEntity> colors;
}
