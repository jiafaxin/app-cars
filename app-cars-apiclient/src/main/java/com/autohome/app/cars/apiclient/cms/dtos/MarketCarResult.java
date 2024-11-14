package com.autohome.app.cars.apiclient.cms.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : zzli
 * @description : 查询上市首发
 * @date : 2024/7/23 14:24
 */
@NoArgsConstructor
@Data
public class MarketCarResult {

    private Integer rowCount;
    private Integer pageCount;
    private Integer pageIndex;
    private List<ItemsDTO> items;

    @NoArgsConstructor
    @Data
    public static class ItemsDTO {
        private Integer id;
        private Integer marketType;
        private Integer seriesId;
        private String seriesName;
        private String seriesImage;
        private Integer brandId;
        private Integer factoryId;
        private String factory;
        private String marketName;
        private Integer newCarType;
        private Integer isMarket;
        private Integer isEnergy;
        private Integer seriesLevelId;
        private String seriesLevelName;
        //车系评级
        private Integer eventLevel;
        private Integer activeTimeType;
        //上市或者首发时间
        private String activeBeginTime;
        //时间描述
        private String activeTimeDesc;
        private String remark;
    }
}
