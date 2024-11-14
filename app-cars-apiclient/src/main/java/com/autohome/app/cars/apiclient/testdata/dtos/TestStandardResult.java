package com.autohome.app.cars.apiclient.testdata.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestStandardResult {
    private int seriesId;
    private int dataId;
    private String seriesName;
    private int seriesMinPrice;
    private int seriesMaxPrice;
    private String seriesPngLogo;

    private int levelId ;
    private int tabId;
    /**
     * 测试车总数；
     */
    private int testedSpecNum;
    private int specId;
    private String specName;
    /**
     * 4，5，6，7 是新源
     */
    private Integer fuleTypeDetail;
    private int fourDrive;
    private int standardId;
    private String standardName;

    /**
     * 车型官方参数
     *
     */
    private List<OfficalParamDataListDTO> officalParamDataList = new ArrayList<>();
    /**
     * 智能硬件配置
     */
    private List<SmartHardwareConfigDTO> smartHardwareConfigList = new ArrayList<>();
    /**
     * 实测单车内容
     */
    private List<TestDataContentListDTO> testDataContentList = new ArrayList<>();
    /**
     * 实测试项内容
     */
    private List<TestDataItemListDTO> testDataItemList = new ArrayList<>();

    /**
     * 官方数据：
     * @Description:
     * @param: null
     * @return null
     * @author xianaichen
     * @date 2023/5/9 16:41
     */
    @Data
    public static  class OfficalParamDataListDTO {
        private int specId;
        private int paramId;
        private String paramName;
        private String paramValue;
    }

    @Data
    public static class SmartHardwareConfigDTO {
        private Integer itemId;
        private String itemName;
        private String subItemName;
    }

    @Data
    public static class TestDataContentListDTO {
        private int id;
        private int contentId;
        private int contentType;
        private String contentValue;
        private String sourceVideoId;
        private String contentName;
    }


    /**
     * 实测项实测内容
     */
    @Data
    public static class TestDataItemListDTO {
        /**
         * 实测项名称
         */
        private String name;
        /**
         * 等级
         */
        private Integer levelId;
        /**
         * 父级id
         */
        private Integer parentId;
        private Integer isShow;
        private Integer isShowVideo;
        private Integer isFourDrive;
        private Integer isResultShow;
        /**
         * 实测项类型
         */
        private Integer contentType;
        /**
         * 实测项单位
         */
        private String contentTypeUnit;

        private Integer sort;
        private String aliasName;

        /**
         * 释义文本
         */
        private String explainText;
        /**
         * 释义图片
         */
        private String explainImg;
        /**
         * 释义业务视频id
         */
        private Integer zixunVideoId;
        private String sourceVideoId;
        /**
         * 实测车型是否无配置：1 不配置，以项下级都不外显
         */
        private int isNotConfig;
        /**
         * 实测项id
         */
        private Integer itemId;
        /**
         * 观点必填值
         */
        private String perspectiveValue;
        private String showVideoValue;
        private String showSourceVideoId;
        private String resultShowValue;
        private String resultSourceVideoId;
        private List<TestDataItemListDTO> testDataItemList = new ArrayList<>();
        private List<testDataItemContentListDTO> testDataItemContentList = new ArrayList<>();
        private String itemImg;


        /**
         * testDataItemContentList 实测项内容素材
         * @Description:
         * @param: null
         * @return null
         * @author xianaichen
         * @date 2023/5/9 16:44
         */
        @Data
        public static class testDataItemContentListDTO{
            /**
             * 素材名称
             */
            private String name;
            /**
             * 实测项id
             */
            private Integer itemId;
            private Integer standardId;
            private Integer isShow;
            private Integer isRequire;
            /**
             * 内容类型
             */
            private Integer contentType;
            /**
             * 单位
             */
            private String contentTypeUnit;
            private Integer contentId;
            private Integer dataId;
            private String contentValue;
            private String sourceVideoId;
        }
    }

}
