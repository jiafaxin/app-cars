package com.autohome.app.cars.apiclient.che168.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class GetCarCityPriceListResult {


    @JsonProperty("citylist")
    private List<CitylistDTO> citylist;
    @JsonProperty("provlist")
    private List<CitylistDTO> provlist;
    @JsonProperty("otherlist")
    private List<CitylistDTO> otherlist;

    @NoArgsConstructor
    @Data
    public static class CitylistDTO {
        @JsonProperty("specid")
        private Integer specid;
        @JsonProperty("minprice")
        private Double minprice;
        @JsonProperty("maxprice")
        private Double maxprice;
        @JsonProperty("seriesid")
        private Integer seriesid;
        @JsonProperty("cunt")
        private Integer cunt;
        @JsonProperty("infoid")
        private Integer infoid;
        @JsonProperty("type")
        private Integer type;
        @JsonProperty("cityid")
        private Integer cityid;
    }

}
