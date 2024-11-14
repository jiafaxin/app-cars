package com.autohome.app.cars.apiclient.che168.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/9/30 9:54
 */
@NoArgsConstructor
@Data
public class UsedCarSearchResult {

    private Integer totalcount;
    private Integer pagesize;
    private Integer pageindex;
    private Integer pagecount;
    private String queryid;
    private Integer styletype;
    private Integer showtype;
    private List<CarDTO> carlist;
    private List<YearDTO> sylist;


    @NoArgsConstructor
    @Data
    public static class CarDTO{

        private Integer infoid;
        private String carname;
        private String cname;
        private Integer dealerid;
        private String mileage;
        private Integer cityid;
        private Integer seriesid;
        private Integer specid;
        private String price;
        private String saveprice;
        private String discount;
        private String firstregyear;
        private Integer fromtype;
        private String imageurl;
        private Integer cartype;
        private Integer bucket;
        private Integer isunion;
        private Integer isoutsite;
        private String videourl;
        private Integer car_level;
        private String downpayment;
        private String url;
        private Integer position;
        private Integer isnewly;
        private String kindname;
        private Integer usc_adid;
        private Integer particularactivity;
        private Integer livestatus;
        private String stra;
        private String springid;
        private Integer followcount;
        private Integer cxctype;
        private Integer isfqtj;
        private Integer isrelivedbuy;
        private Integer photocount;
        private Integer isextwarranty;
        private String act_discount;
        private String environmental;
        private String direct_descent_format;
        private String tag_config;
        private Integer flowcar;
        private String displacement;
        private String liveurl;
        private ConsignmentDTO consignment;
        private Integer leads;
        private String cstencryptinfo;
        private String pv_extstr;
        private CartagsDTO cartags;
        private int isrecommend;

        @NoArgsConstructor
        @Data
        public static class ConsignmentDTO {
            private Integer isconsignment;
            private Integer endtime;
            private String imurl;
            private Integer isyouxin;
        }

        @NoArgsConstructor
        @Data
        public static class CartagsDTO {
            private List<TagDTO> p1;
            private List<TagDTO> p2;
            private List<TagDTO> p3;
            private List<TagDTO> p4;

            @NoArgsConstructor
            @Data
            public static class TagDTO {
                private String title;
                private String bg_color;
                private String bg_color_end;
                private String font_color;
                private String border_color;
                private String stype;
                private Integer sort;
            }
        }
    }

    @NoArgsConstructor
    @Data
    public static class YearDTO {
        private int syid;
        private String syname;
        private int year;
        private String lowprice;
        private String alllowprice;
    }
}
