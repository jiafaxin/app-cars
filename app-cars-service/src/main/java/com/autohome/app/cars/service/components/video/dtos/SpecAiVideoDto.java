package com.autohome.app.cars.service.components.video.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SpecAiVideoDto {

    private int specId;
    private Map<Integer,SpecAiVideoResult> videoInfoMap = new HashMap<>();


    @Data
    public static class SpecAiVideoResult {

        private int seriesid;
        private int specid;
        private int configid;
        private String configname;
        private String videoid;

    }

}
