package com.autohome.app.cars.service.components.che168.dtos;

import lombok.Data;

@Data
public class SeriesCityUsedCarExchangeDto {

    private S2scExchangeResult_Data datasell;

    @Data
    public static class S2scExchangeResult_Data {
        private String url;
        private String maintitle;
        private String subhead;
        private String iconurl;
    }
}
