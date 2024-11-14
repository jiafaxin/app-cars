package com.autohome.app.cars.service.components.dealer.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeriesCityCpsDto {
    int seriesId;
    int cityId;

    BigDecimal price;
}
