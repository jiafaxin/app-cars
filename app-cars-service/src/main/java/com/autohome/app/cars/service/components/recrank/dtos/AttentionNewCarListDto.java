package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/27
 */
@Data
public class AttentionNewCarListDto {


    private String dt;

    /**
     * 数据更新标识（即数据组生成数据并同步到db后，取该时刻的时间戳）
     */
    private String updateFlag;

    private int pageIndex;

    private int pageSize;

    private int count;

    private List<ResultDto> seriesList = new ArrayList<>();

    @Data
    public static class ResultDto {

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

}
