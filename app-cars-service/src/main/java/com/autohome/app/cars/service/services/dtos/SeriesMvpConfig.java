package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@Component
@Slf4j
public class SeriesMvpConfig {

    @Value("${series_mvp_confg:}")
    private String json;

    @JsonProperty("btnTitle")
    private String btnTitle;
    @JsonProperty("ruletype")
    private Integer ruletype;
    @JsonProperty("seriesList")
    private List<SeriesListDTO> seriesList;
    @JsonProperty("textlist")
    private List<TextlistDTO> textlist;
    @JsonProperty("textBList")
    private List<TextBListDTO> textBList;

    @NoArgsConstructor
    @Data
    public static class SeriesListDTO {
        @JsonProperty("seriesId")
        private Integer seriesId;
        @JsonProperty("excludeCity")
        private List<Integer> excludeCity;
    }

    @NoArgsConstructor
    @Data
    public static class TextlistDTO {
        @JsonProperty("abversion")
        private String abversion;
        @JsonProperty("amount")
        private String amount;
        @JsonProperty("text_A")
        private String textA;
        @JsonProperty("text_B")
        private String textB;
        @JsonProperty("text_C")
        private String textC;
        @JsonProperty("text_D")
        private String textD;
        @JsonProperty("text_E")
        private String textE;
        @JsonProperty("text_F")
        private String textF;
        @JsonProperty("linktype")
        private Integer linktype;
    }

    @NoArgsConstructor
    @Data
    public static class TextBListDTO {
        @JsonProperty("seriesid")
        private Integer seriesid;
        @JsonProperty("amount")
        private String amount;
        @JsonProperty("text")
        private String text;
    }

    public SeriesMvpConfig get(){
        if(StringUtils.isBlank(json)){
            return null;
        }
        try {
            return JsonUtil.toObject(json,SeriesMvpConfig.class);
        }catch (Exception e){
            log.error("SeriesMvpConfig 序列化失败",e);
            return null;
        }
    }
}
