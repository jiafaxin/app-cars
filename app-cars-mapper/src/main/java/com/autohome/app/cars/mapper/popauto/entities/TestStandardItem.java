package com.autohome.app.cars.mapper.popauto.entities;


import lombok.Data;

@Data
public class TestStandardItem {
    private Integer id;

    private Integer standardId;
    private String name;
    private int levelId;
    private Integer parentId;

    private int isShow;

    private int isRequire;
    private int isShowVideo;
    private int isFourDrive;
    private int isResultShow;
    private int contentType;
    private String contentTypeUnit;

    private Integer sort;
    private String aliasName;

    private String remark;
    /**
     * 释义文本
     */
    private String explainText;
    /**
     * 释义图片
     */
    private String explainImg;
    /**
     * 释义资讯视频id
     */
    private Integer zixunVideoId;
    /**
     * 释义源视频id
     */
    private String sourceVideoId;
    /**
     * 实测项刻画
     */
    private String ItemImg;
    /**
     * 实测车型是否无配置
     */
    private int isNotConfig;

    private Integer zixunVideoIdItem;
    private String sourceVideoIdItem = "";
    private int isRequireNecessary;
    private int isShowVideoNecessary;
}
