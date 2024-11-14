package com.autohome.app.cars.service.components.newcar.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : zzli
 * @description : 新车日历-11.63.3版本的
 * @date : 2024/6/7 10:35
 */
@NoArgsConstructor
@Data
public class CarCalendarDto {
    private Date specpublishtime;
    /**
     * 资讯车系评级：S、A、B
     */
    private String eventLevel;
    private PicDTO pic;
    private ParamDTO param;
    private PriceDTO price;
    //在上市时间的前面又加了一个发布时间
    private PriceDTO publishdate;
    private CarsalesDTO carsales;
    private CarfriendshareDTO carfriendshare;
    /**
     * 首测公布(新车评测)
     */
    private PriceDTO evaluating;
    /**
     * 新车首试
     */
    private PriceDTO testdrive;
    /**
     * 新车预告
     */
    private PriceDTO carpreview;

    @NoArgsConstructor
    @Data
    public static class PicDTO {
        private Integer typecode;
        private Date date;
        private String url;
        private String name;
        private CarddataDTO carddata;

        public PicDTO(Integer typecode, String name, CarddataDTO carddata) {
            this.typecode = typecode;
            this.name = name;
            this.carddata = carddata;
        }

        @NoArgsConstructor
        @Data
        public static class CarddataDTO {
            private int piccount;
            private List<picitemsDTO> picitems = new ArrayList<>();
            private List<OutcoloritemsDTO> outcoloritems = new ArrayList<>();
            private List<SpecPic> specpicList = new ArrayList<>();

            @NoArgsConstructor
            @Data
            public static class OutcoloritemsDTO {
                private String colorname;
                private String colorpic;
                private String url;
            }

            @NoArgsConstructor
            @Data
            public static class picitemsDTO {
                private String pic;
                private String url;
            }

            @Data
            @NoArgsConstructor
            public static class SpecPicItemDto {
                private String image;
                private String linkurl;
                private String colortext;
            }

            @Data
            @NoArgsConstructor
            public static class SpecPic {
                private String specname;
                private List<SpecPicItemDto> list = new ArrayList<>();
            }
        }
    }

    @NoArgsConstructor
    @Data
    public static class ParamDTO {
        private Integer typecode;
        private Date date;
        private String url;
        private String name;
        private CarddataDTO carddata;

        public ParamDTO(Integer typecode, Date date, String url, String name, CarddataDTO carddata) {
            this.typecode = typecode;
            this.date = date;
            this.url = url;
            this.name = name;
            this.carddata = carddata;
        }

        @NoArgsConstructor
        @Data
        public static class CarddataDTO {
            private int speccount;
            private List<SpecconfigitemsDTO> specconfigitems = new ArrayList<>();

            @NoArgsConstructor
            @Data
            public static class SpecconfigitemsDTO {
                private Integer specid;
                private String specname;
                private List<ConfiglistDTO> configlist = new ArrayList<>();
                private String url;
                /**
                 * 非全新车系，新老款对比
                 */
                private String summaryconfigdif;

                @NoArgsConstructor
                @Data
                public static class ConfiglistDTO {
                    private String paramname;
                    private String paramvalue;
                }
            }
        }
    }

    @NoArgsConstructor
    @Data
    public static class PriceDTO {
        private Integer typecode;
        private Date date;
        private String url;
        private String name;
        private CarddataDTO carddata;

        public PriceDTO(Integer typecode, Date date, String name) {
            this.typecode = typecode;
            this.date = date;
            this.name = name;
        }

        @NoArgsConstructor
        @Data
        public static class CarddataDTO {
            private String title;
            private String img;
            private boolean isvideo;
            private int duration;
        }
    }

    @NoArgsConstructor
    @Data
    public static class CarsalesDTO {
        private Integer typecode;
        private Date date;
        private String url;
        private String name;
        private CarddataDTO carddata;

        public CarsalesDTO(Integer typecode, Date date, String name, CarddataDTO carddata) {
            this.typecode = typecode;
            this.date = date;
            this.name = name;
            this.carddata = carddata;
        }

        @NoArgsConstructor
        @Data
        public static class CarddataDTO {
            private String currentmonth;
            private int currentmonthsales;
            private String lastmonth;
            private int lastmonthsales;
            private String rn;
            private int currentweeksales;
            private int lastweeksales;
        }
    }

    @NoArgsConstructor
    @Data
    public static class CarfriendshareDTO {
        private Integer typecode;
        private Date date;
        private String url;
        private String name;
        private CarddataDTO carddata;

        public CarfriendshareDTO(Integer typecode, Date date, String url, String name, CarddataDTO carddata) {
            this.typecode = typecode;
            this.date = date;
            this.url = url;
            this.name = name;
            this.carddata = carddata;
        }

        @NoArgsConstructor
        @Data
        public static class CarddataDTO {
            private String title;
            private String img;
            private boolean isvideo;
            private int duration;
        }
    }

    // 检查至少有一个对象不为空的方法
    public boolean checkAtLeastOneObjectNotNull() {
        return pic != null
                || param != null
                || price != null
                || carsales != null
                || carfriendshare != null || evaluating != null || testdrive != null || carpreview != null;
    }
}
