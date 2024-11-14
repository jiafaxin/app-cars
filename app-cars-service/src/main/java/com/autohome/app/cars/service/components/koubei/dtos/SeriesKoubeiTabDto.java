package com.autohome.app.cars.service.components.koubei.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class SeriesKoubeiTabDto {
    private String average;

    private List<SemanticSummary> semanticSummaries = new ArrayList<>();

    private CopyOnWriteArrayList<Evaluation> evaluations = new CopyOnWriteArrayList<>();

    @Data
    public static class SemanticSummary {
        private int volume;
        private int tabid;
        private int summarykey;
        private int sentimentkey;
        private boolean iselectronic;
        private String combination;
    }

    @Data
    public static class Evaluation {
        private String posttime;
        private int specid;
        private String buyplace;
        private int distance;
        private String buyprice;
        private int helpfulcount;
        private int powertypefeelingid;
        private int recommend;
        private int userid;
        private String carownername;
        private int powertype;
        private String isbattery;
        private int id;
        private int userBigVLevel;
        private int tabid;
        private int actual_battery_consumption;
        private List<String> piclist = new ArrayList<>();
        private String commentlinkurl;
        private int carownerlevels;
        private String specname;
        private String feeling_summary;
        private Double actual_oil_consumption;
        private int commentcount;
        private List<ContentsDTO> contents = new ArrayList<>();
        private String userimage;
        private int bigV;
        private int viewcount;
        private String linkurl;
        private int isauth;
        private String username;
        private int koubeitype;
        @Data
        public static class ContentsDTO {
            private int structuredid;
            private String structuredname;
            private String content;
        }
    }
}
