package com.autohome.app.cars.service.components.recrank.dtos.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : TODO
 * @date : 2024/11/4 15:48
 */
@Data
public class RankResultNewDto {
    private int pageindex;
    private int pagecount;
    private int pagesize;
    private List<RankListItemDto> list = new ArrayList<>();

    @Data
    public static class RankListItemDto {
        private String seriesid;
        private String levelId;
        //private int rankNum;
        private int rankchange;
        private String seriesimage;
        private String seriesname;
        private String scorevalue;
        private String scoretip;
        private String priceinfo;
        private Long salecount;

        private String rcmtext;
        private String rcmdesc;

        private String minpricespecname;
        private Integer minpricespecid;
        private Integer brandid;
        private String brandname;
        private String brandimage;
        private Integer energytype;

        private int minPrice;
        private int maxPrice;
        /**
         * 厂商类型
         */
        private String manuType;

        /**
         * 能源类型
         */
        private String fuelTypes;
        /**
         * 本月排名 销量相同会重复
         */
        private int rn;
        /**
         * 本月排名 销量相同不会重复
         */
        private int rnNum;
        /**
         * 上月排名
         */
        private int preRankNum;

        /**
         * 车系销售状态
         */
        private int state;
    }
}
