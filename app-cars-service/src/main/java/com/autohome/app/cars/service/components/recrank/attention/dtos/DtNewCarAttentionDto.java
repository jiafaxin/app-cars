package com.autohome.app.cars.service.components.recrank.attention.dtos;

import lombok.Data;

/**
 * @author chengjincheng
 * @date 2024/7/16
 */
@Data
public class DtNewCarAttentionDto {

    /**
     * 车系id
     */
    private int seriesId;

    /**
     * 车系名称
     */
    private String seriesName;

    /**
     * 车系级别
     */
    private int levelId;

    /**
     * 关注度数值
     */
    private Integer att;

    /**
     * 上市日期
     */
    private String onTime;

    /**
     * 标签名称
     */
    private String seriesTag;

    /**
     * 标签Id
     */
    private Integer seriesTagId;

    /**
     * 文章Id
     */
    private Integer articleId;

    /**
     * 排名
     */
    private Integer rankNum;

    /**
     * 排名变化
     */
    private Integer ranChange;
}
