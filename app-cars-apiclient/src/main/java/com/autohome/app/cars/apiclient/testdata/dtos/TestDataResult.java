package com.autohome.app.cars.apiclient.testdata.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class TestDataResult {

    @JsonProperty("specId")
    private Integer specId;
    @JsonProperty("fueltypedetail")
    private Integer fueltypedetail;
    @JsonProperty("standardId")
    private Integer standardId;
    @JsonProperty("dataId")
    private Integer dataId;
    @JsonProperty("isGenerate")
    private int isGenerate;
    @JsonProperty("testItemlist")
    private List<TestItemlistDTO> testItemlist;

    @NoArgsConstructor
    @Data
    public static class TestItemlistDTO {
        @JsonProperty("name")
        private String name;
        @JsonProperty("showValue")
        private String showValue;
    }
}
