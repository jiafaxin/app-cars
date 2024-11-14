package com.autohome.app.cars.service.components.hangqing.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/9/20 20:57
 */
@Data
public class SpecCityPriceHistoryDto {
    private int cityId;
    private int specId;
    private List<SpecCityPriceItemDto> list;

    @Data
    public static class SpecCityPriceItemDto {
        private LocalDate date;
        private int newsPrice;
    }
}
