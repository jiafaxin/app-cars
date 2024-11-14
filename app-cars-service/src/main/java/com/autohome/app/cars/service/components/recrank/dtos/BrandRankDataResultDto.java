package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 品牌月榜&品牌周榜Dto
 *
 * @author zhangchengtao
 * @date 2024/4/29 9:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandRankDataResultDto {
    private List<RankDataDto> dataList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RankDataDto {

        /**
         * 车系Id
         */
        private Integer seriesId;

        /**
         * 车系级别
         */
        private String levelId;
        /**
         * 品牌图片
         */
        private String brandImage;

        /**
         * 品牌名称
         */
        private String brandName;
        /**
         * 车系名称
         */
        private String seriesName;

        /**
         * 车系品牌ID
         */
        private Integer brandId;

        /**
         * 车系销量
         */
        private long saleCount;


        /**
         * 新能源类型
         */
        private int energyType;

        /**
         * 厂商类型
         */
        private String manuType;

        /**
         * 销量月份
         */
        private String month;

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

        public static RankDataDto getInstance(Integer brandId, String brandName, String brandImage, String manuType, long saleCount) {
            RankDataDto rankDataDto = new RankDataDto();
            rankDataDto.setBrandId(brandId);
            rankDataDto.setBrandName(brandName);
            rankDataDto.setBrandImage(brandImage);
            rankDataDto.setSaleCount(saleCount);
            rankDataDto.setManuType(manuType);
            return rankDataDto;
        }
    }
}
