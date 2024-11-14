package com.autohome.app.cars.apiclient.dingding.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by dx on 2024/9/5
 */
@Data
public class DingDingApiResult {
    @JsonProperty("Status")
    private String Status;
    @JsonProperty("Result")
    private String Result;
}
