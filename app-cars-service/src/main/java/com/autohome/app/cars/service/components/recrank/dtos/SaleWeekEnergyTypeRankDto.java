package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by dx on 2024/7/9
 */
@NoArgsConstructor
@Data
public class SaleWeekEnergyTypeRankDto {
    private int seriesId;
    private List<WeekDayList> list;

    @NoArgsConstructor
    @Data
    public static class WeekDayList {
        private String week_day;
        private List<SaleWeekEnergyTypeRankListDto> list;
    }
}
