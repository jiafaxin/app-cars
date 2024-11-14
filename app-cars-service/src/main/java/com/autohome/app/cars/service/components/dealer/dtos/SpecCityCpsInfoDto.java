package com.autohome.app.cars.service.components.dealer.dtos;

import com.autohome.app.cars.apiclient.dealer.dtos.SpecCityCpsInfoResult;
import lombok.Data;

@Data
public class SpecCityCpsInfoDto {

    private int cityId;
    private int specId;
    private SpecCityCpsInfoResult cpsInfo;

}
