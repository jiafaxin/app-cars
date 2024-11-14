package com.autohome.app.cars.service.services.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/10/15 9:57
 */
@Data
public class SubsidyTradeInPartDto {
    private int partId;
    private String partName;
    private List<PartInfo> partInfoList;

    @Data
    public static class PartInfo {
        private int seriesId;
        private String seriesName;
        private String seriesImage;
        private String priceInfo;
        private String downPriceInfo;
        private String btnTitle;
        private String btnPrice;
    }
}
