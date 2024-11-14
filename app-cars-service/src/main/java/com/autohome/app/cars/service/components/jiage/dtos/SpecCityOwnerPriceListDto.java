package com.autohome.app.cars.service.components.jiage.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/1
 */
@Data
public class SpecCityOwnerPriceListDto {
    private List<SpecCityOwnerPriceDto> result = new ArrayList<>();

    @Data
    public static class SpecCityOwnerPriceDto {
        private int total;
        private int specId;
        private int avg;
    }
}
