package com.autohome.app.cars.service.components.dealer.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeriesDriveDto {
    int seriesId;

    List<Integer> testDriveCitys = new ArrayList<>();
    List<Integer> homeTestDriveCitys = new ArrayList<>();
}
