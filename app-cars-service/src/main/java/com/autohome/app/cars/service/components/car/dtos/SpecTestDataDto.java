package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.apiclient.testdata.dtos.TestStandardResult;
import lombok.Data;

@Data
public class SpecTestDataDto {

    private int specId;
    private TestStandardResult testStandardResult;

}
