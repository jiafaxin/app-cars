package com.autohome.app.cars.service.components.recrank.koubei.dto;

import lombok.Data;

/**
 * @author chengjincheng
 * @date 2024/7/12
 */
@Data
public class KoubeiRankDto {

    private int seriesId;

    private int number;

    private int rank;

    private float score;

    private int levelId;

    private int isNewEnergy;

    // 能源类型
    private String fuelTypes;

    // 自主/进口/合资/独资
    private String fctTypeId;

    private int minPrice;

    private int maxPrice;

}
