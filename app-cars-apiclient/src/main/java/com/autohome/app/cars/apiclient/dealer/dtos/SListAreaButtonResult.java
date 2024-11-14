package com.autohome.app.cars.apiclient.dealer.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : zzli
 * @description : 区域工具组合接口result
 * @date : 2024/6/27 13:35
 */
@NoArgsConstructor
@Data
public class SListAreaButtonResult {
    @JsonProperty("tag")
    private String tag;
    @JsonProperty("group")
    private String group;
    @JsonProperty("specId")
    private Integer specId;
    @JsonProperty("buttonList")
    private List<ButtonListDTO> buttonList;

    @NoArgsConstructor
    @Data
    public static class ButtonListDTO {
        @JsonProperty("btnType")
        private Integer btnType;
        @JsonProperty("mainText")
        private String mainText;
        @JsonProperty("subText")
        private String subText;
        @JsonProperty("cornerText")
        private String cornerText;
        @JsonProperty("windowType")
        private Integer windowType;
        @JsonProperty("telNO")
        private String telNO;
        @JsonProperty("imSchema")
        private String imSchema;
        @JsonProperty("sourceId")
        private Integer sourceId;
        @JsonProperty("specId")
        private Integer specId;
        @JsonProperty("ext")
        private String ext;
        @JsonProperty("url")
        private String url;
        @JsonProperty("isSupply")
        private Integer isSupply;
    }
}
