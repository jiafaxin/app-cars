package com.autohome.app.cars.apiclient.che168.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GetUsedCarExchangeInfoResult {

    private S2scExchangeResult_Data datasell;

    @NoArgsConstructor
    @Data
    public static class S2scExchangeResult_Data {
        private String url;
        private String maintitle;
        private String subhead;
        private String iconurl;
    }
}
