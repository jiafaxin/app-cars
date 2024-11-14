package com.autohome.app.cars.apiclient.che168.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/11/5 16:51
 */
@NoArgsConstructor
@Data
public class CitySeriesCarsWithPic {


    @JsonProperty("pageindex")
    private Integer pageindex;
    @JsonProperty("pagesize")
    private Integer pagesize;
    @JsonProperty("totalcount")
    private Integer totalcount;
    @JsonProperty("pagecount")
    private Integer pagecount;
    @JsonProperty("list")
    private List<ListDTO> list;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        @JsonProperty("specid")
        private Integer specid;
        @JsonProperty("specname")
        private String specname;
        @JsonProperty("cars")
        private List<CarsDTO> cars;

        @NoArgsConstructor
        @Data
        public static class CarsDTO {
            @JsonProperty("infoid")
            private Integer infoid;
            @JsonProperty("carname")
            private String carname;
            @JsonProperty("dealerid")
            private Integer dealerid;
            @JsonProperty("price")
            private String price;
            @JsonProperty("firprice")
            private String firprice;
            @JsonProperty("regdate")
            private String regdate;
            @JsonProperty("mileage")
            private String mileage;
            @JsonProperty("cityname")
            private String cityname;
            @JsonProperty("tag")
            private List<String> tag;
            @JsonProperty("detailurl")
            private String detailurl;
            @JsonProperty("pics")
            private List<PicsDTO> pics;
            @JsonProperty("carlisturl")
            private String carlisturl;

            @NoArgsConstructor
            @Data
            public static class PicsDTO {
                @JsonProperty("url")
                private String url;
                @JsonProperty("piclisturl")
                private String piclisturl;
            }
        }
    }
}
