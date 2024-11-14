package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

@Data
public class TestDataEntity {

    private Integer id;
    private Integer seriesId;
    private Integer specId;
    private Integer standardId;
    /**
     * 相同车型生成：来源id
     */
    private Integer fromId;

    /**
     * 是否相同车型生成：0否 1是
     */
    private int isGenerate;

    private int isPublish;
    private int is_del;
}
