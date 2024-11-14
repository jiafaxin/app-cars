package com.autohome.app.cars.apiclient.rank.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class HedgeRankResult {


    private Integer pageindex;

    private Integer pagecount;

    private Integer pagesize;

    private String saleranktip;

    private String morescheme;

    private String scenetitle;

    private String scenesubtitle;

    private List<ListDTO> list;

    @NoArgsConstructor
    @Data
    public static class ShareinfoDTO {

        private String title;

        private String subtitle;

        private String url;

        private String logo;
    }

    @NoArgsConstructor
    @Data
    public static class ListDTO {

        private Integer seriesid;

        private String rank;

        private Integer rankchange;

        private String seriesimage;

        private String seriesname;

        private String scorevalue;

        private String scoretip;

        private String priceinfo;

        private String linkurl;

        private Integer salecount;

        private Integer energetype;

        private String historylinkname;

        private String historylinkurl;

        private String rightpricetitle;

        private String pricelinkurl;

        private String rcmtext;

        private String rcmdesc;

        private String specname;

        private String righttextone;

        private String righttexttwo;

        private String righttexttwolinkurl;
    }
}
