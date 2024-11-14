package com.autohome.app.cars.service.components.car.dtos;


import lombok.Data;

import java.util.List;

@Data
public class SeriesInfoDetailIDto{

    private String fcPy;

    private int placeNum;

    private int fctId;

    private String fctName;

    private List<SeriesDetailDto> seriesDetailDtoList;
}
