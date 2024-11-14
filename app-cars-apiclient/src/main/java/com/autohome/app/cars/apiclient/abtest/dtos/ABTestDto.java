package com.autohome.app.cars.apiclient.abtest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class ABTestDto {
    private Integer returncode;
    private String message;
    private ResultDTO result;

    @Data
    public static class ResultDTO {
        private List<ListDTO> list;
        private String baseline;

        @Data
        public static class ListDTO {
            private Boolean result;
            private String tvStyle;
            private String variable;
            private String version;
        }
    }
}
