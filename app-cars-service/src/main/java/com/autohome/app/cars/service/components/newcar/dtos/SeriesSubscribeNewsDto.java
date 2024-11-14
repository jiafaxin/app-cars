package com.autohome.app.cars.service.components.newcar.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/8/12 21:32
 */
@Data
public class SeriesSubscribeNewsDto {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SeriesPriceDownDto extends SeriesSubscribeNewsDto {
        private int curMinPrice;
        private int curMaxPrice;
        private int prevMinPrice;
        private int prevMaxPrice;
        private int minPriceDown;
        private int maxPriceDown;
        /**
         * 最大降幅车型
         */
        private int specId;

        /**
         * 车型名称
         */
        private String specName;
        /**
         * 降价车型数
         */
        private int count;

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SpecPriceDownDto extends SeriesSubscribeNewsDto {
        /**
         * 降价后价格
         */
        private int curPrice;
        /**
         * 降价前价格
         */
        private int prevPrice;

        /**
         * 车型名称
         */
        private String specName;

    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CmsNewsDto extends SeriesSubscribeNewsDto {
        private Integer authorId;
        private String authorImg;
        private String authorName;
        private Long globalId;
        private Integer bizId;
        private String title;
        private LocalDateTime publishTime;
        private String mainDataType;
        private String scheme;
        private List<String> imgUrlList;

    }

    /**
     * 车系图片
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PicDTO extends SeriesSubscribeNewsDto {
        private int picCount;
        private String linkurl="xxxxxx";
        private Date publishTime;
        private List<picItemsDTO> picItems = new ArrayList<>();

        @NoArgsConstructor
        @Data
        public static class picItemsDTO {
            private String pic;
            private String url;
            /**
             * 有颜色是颜色分组，没有则是图片平铺
             */
            private String colorName;
        }
    }

    /**
     * 上市价格
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PriceDto extends SeriesSubscribeNewsDto {
        //上市时间
        private Date publishTime;
        private String price;
        private int specNum;
        //文章相关
        private String source;//之家原创
        private String authorName;
        private Integer bizId;
        private String title;
        private String mainDataType;
        private String scheme;
        private List<String> imgUrlList=new ArrayList<>();
    }

    /**
     * 提车作业分享
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class friendShareDTO extends SeriesSubscribeNewsDto{
        private Date publishTime;
        private String authorName;
        private String authorCertifiedCar;//认证车
        private String authorCertifiedCarIcon;//认证车标
        private String title;
        private String scheme;
        private List<String> imgUrlList=new ArrayList<>();
        private boolean isvideo;
        private int duration;
        private Integer topicid;
        private boolean carowner;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class RankInfoDto extends SeriesSubscribeNewsDto {
        private long lastSaleCount;
        private long currentSaleCount;
        private String date;
        private String scheme;
        /**
         * 排名
         */
        private int rnnum;
        private String dateValue;
    }

    /**
     * 参配
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CarParamDTO extends SeriesSubscribeNewsDto{
        private int speccount;
        private String url;
        private List<SpecconfigitemsDTO> specconfigitems = new ArrayList<>();

        @Data
        public static class SpecconfigitemsDTO {
            private Integer specid;
            private String specname;
            private List<ConfiglistDTO> configlist = new ArrayList<>();
            //private String url;
            @Data
            public static class ConfiglistDTO {
                private String paramname;
                private String paramvalue;
            }
        }
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class AssessReport extends SeriesSubscribeNewsDto {
        private Integer authorId;
        private String authorImg;
        private String authorName;
        private Long globalId;
        private Integer bizId;
        private String title;
        private LocalDateTime publishTime;
        private String mainDataType;
        private String scheme;
        private List<String> imgUrlList;

    }
}
