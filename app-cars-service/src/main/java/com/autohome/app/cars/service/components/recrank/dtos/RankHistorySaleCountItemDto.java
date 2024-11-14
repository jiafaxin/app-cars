package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RankHistorySaleCountItemDto {
    private int type;
    private LocalDate month;
    private int saleCount;

    public static RankHistorySaleCountItemDto getInstance(int type, LocalDate month, int saleCount) {
        return new RankHistorySaleCountItemDto(type, month, saleCount);
    }
}
