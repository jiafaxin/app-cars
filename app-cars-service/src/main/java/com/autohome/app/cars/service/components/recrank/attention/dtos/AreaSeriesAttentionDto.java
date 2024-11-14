package com.autohome.app.cars.service.components.recrank.attention.dtos;

import lombok.Data;

@Data
public class AreaSeriesAttentionDto {
    int seriesId;
    int levelId;
    long att;
    int isNewEnergy;
    // 能源类型
    String fuelTypes;
    // 自主/进口/合资/独资
    String fctTypeId;
    int minPrice;
    int maxPrice;
}
