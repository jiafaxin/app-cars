package com.autohome.app.cars.apiclient.owner.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class PlayCarCardResult {

    private List<TabsDTO> tabs = new ArrayList<>();
    private List<ListDTO> list = new ArrayList<>();


    @Data
    public static class TabsDTO {
        private Integer tabId;
        private Integer sortValue;
        private String tabName;
        private String sourceUrl;
    }

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        private CarddataDTO carddata;
        private String mainDataType;
        private Integer tabid;
        private ExtensionDTO extension;
        private Integer bizId;

        @NoArgsConstructor
        @Data
        public static class CarddataDTO {
            private CardinfoDTO cardinfo;
            private Integer cardtype;
            private Integer mediatype;
//            private PvdataDTO pvdata;

            @NoArgsConstructor
            @Data
            public static class CardinfoDTO {
                private List<ImgDTO> img;
                private String imgicon;
                private String playtime;
                private List<TaginfoDTO> taginfo;
                private String title;

                @NoArgsConstructor
                @Data
                public static class ImgDTO {
                    private String url;
                }

                @NoArgsConstructor
                @Data
                public static class TaginfoDTO {
                    private String iconurl;
                    private Integer position;
                    private String scheme;
                    private String text;
                    private Integer styletype;
                    private String bgcolor;
                    private String fontcolor;
                }
            }
//
//            @NoArgsConstructor
//            @Data
//            public static class PvdataDTO {
//                private String pvclick;
//                private String pvlight;
//            }
        }

        @NoArgsConstructor
        @Data
        public static class ExtensionDTO {
            private String componetid;
            private Integer jumptype;
            //            private String modulename;
            private ObjinfoDTO objinfo;
            private String scheme;
            //            private Integer schemecontainertype;
            private Integer istop;

            @NoArgsConstructor
            @Data
            public static class ObjinfoDTO {
                private Integer id;
                private String title;
            }
        }
    }
}
