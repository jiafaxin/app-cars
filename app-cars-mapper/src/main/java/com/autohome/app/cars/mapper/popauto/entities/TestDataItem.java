package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.util.List;

@Data
public class TestDataItem extends TestStandardItem {
    private Integer dataId;

    private Integer standardId;
    private Integer itemId;

    private String itemName;
    private String perspectiveValue;
    private String showVideoValue;
    private String showSourceVideoId;
    private String resultShowValue;
    private String resultSourceVideoId;

    private String itemJson;

    private String itemJson165;

    private String itemJson185;

    private int isThird;


    //新加展示字段

    /**
     * 业务逻辑判断用
     */
    private int haveVal;
    /**
     * 嵌套实测子项
     */
    private List<TestDataItem> testDataItemList;

    /**
     * 实测项素材列表
     */
    private List<TestDataItemContent> testDataItemContentList;
}
