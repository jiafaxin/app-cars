package com.autohome.app.cars.apiclient.rank.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ClueCheckAllResultDto {

    private String message;
    private List<ResultDTO> result;
    private Integer returncode;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        private Integer seriesid;
        private Integer specid;
    }
}
