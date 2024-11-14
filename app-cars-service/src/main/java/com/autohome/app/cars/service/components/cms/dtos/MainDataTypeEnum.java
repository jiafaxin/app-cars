package com.autohome.app.cars.service.components.cms.dtos;


import org.apache.commons.lang3.StringUtils;

/**
 * 主数据接口datatype枚举
 * wiki: http://maindataupstream.api.autohome.com.cn/doc
 * @author liuchao
 * @date 2020/06/01
 */
public enum MainDataTypeEnum {

    /**
     * 原创文章
     */
    CMS("cms"),

    /**
     * 原创视频
     */
    VIDEO("video"),

    /**
     * 车家号内容
     */
    CHEJIAHAO("chejiahao"),

    /**
     * 文章对应的话题
     */
    CMS_TOPIC("cms_topic"),

    /**
     * 原创ah100
     */
    CMS_AH100("cms_ah100"),

    /**
     * 直播业务线
     */
    LIVE("zhibo");


    private String value;

    public String getValue() {
        return value;
    }

    MainDataTypeEnum(String value) {
        this.value = value;
    }

    public static MainDataTypeEnum getByValue(String value){
        if (StringUtils.isNotEmpty(value)) {
            for (MainDataTypeEnum mainDataTypeEnum : MainDataTypeEnum.values()) {
                if (StringUtils.equals(mainDataTypeEnum.value,value)) {
                    return mainDataTypeEnum;
                }
            }
        }
        return null;
    }
}
