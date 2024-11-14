package com.autohome.app.cars.service.services.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class SeriesCandyBeansConfig {

    private Integer isopen;
    private List<ListDTO> list;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        private Integer seriesid;
        private String linkurl;
    }
}
