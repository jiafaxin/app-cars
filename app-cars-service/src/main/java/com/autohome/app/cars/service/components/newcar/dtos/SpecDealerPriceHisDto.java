package com.autohome.app.cars.service.components.newcar.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhangchengtao
 * @date 2024/9/19 16:33
 */
@Data
@AllArgsConstructor
public class SpecDealerPriceHisDto {
    private int specId;
    private int seriesId;
    private int cityId;
    private String specName;
    private LocalDate date;
    private int price;

    public static SpecDealerPriceHisDto getInstance(int specId, int seriesId,int cityId, String specName, LocalDate date, int price) {
        return new SpecDealerPriceHisDto(specId, seriesId, cityId, specName, date, price);
    }

}
