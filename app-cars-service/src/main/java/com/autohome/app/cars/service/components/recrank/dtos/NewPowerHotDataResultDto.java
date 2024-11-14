package com.autohome.app.cars.service.components.recrank.dtos;

import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.uv.dto.EsSeriesUvItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新势力热度榜数据
 */
@Data
public class NewPowerHotDataResultDto {
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
         * 热门车型ID
         */
        private Integer hostSpecId;
        /**
         * 车系级别
         */
        private int levelId;
        /**
         * 车系图片
         */
        private String seriesImage;
        /**
         * 车系名称
         */
        private String seriesName;
        /**
         * 车系UV数量
         */
        private long uvCount;
        /**
         * 车系最低价格
         */
        private int minPrice;

        /**
         * 车系最高价格
         */
        private int maxPrice;

        /**
         * 本月排名 销量相同会重复
         */
        private int rn;
        /**
         * 本月排名 销量相同不会重复
         */
        private int rnNum;

        /**
         * 车系销售状态
         */
        private int state;

        public static RankDataDto getInstance(EsSeriesUvItemDto itemDto, SeriesDetailDto detailDto) {
            RankDataDto dto = new RankDataDto();
            long count = BigDecimal.valueOf(itemDto.getCount() * 1.5).intValue() + 333L;
            dto.setSeriesId(itemDto.getSeriesId());
            dto.setUvCount(count);
            dto.setLevelId(detailDto.getLevelId());
            dto.setHostSpecId(detailDto.getHotSpecId());
            dto.setSeriesName(detailDto.getName());
            dto.setSeriesImage(detailDto.getPngLogo());
            dto.setMinPrice(detailDto.getMinPrice());
            dto.setMaxPrice(detailDto.getMaxPrice());
            dto.setState(detailDto.getState());
            return dto;
        }
    }
}
