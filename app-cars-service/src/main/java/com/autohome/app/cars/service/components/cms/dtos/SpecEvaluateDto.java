package com.autohome.app.cars.service.components.cms.dtos;

import com.autohome.app.cars.apiclient.cms.dtos.SpecEvaluateItemResult;
import lombok.Data;

@Data
public class SpecEvaluateDto {

    private int specId;
    private SpecEvaluateItemResult evaluateItemResult;

}
