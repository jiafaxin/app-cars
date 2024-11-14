package com.autohome.app.cars.service.components.recrank.safety.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/7/15
 */
@Data
public class SafetyRankDto {

    private int seriesId;

    private String compScore;

    private String starScore;

    private List<Item> itemList;

    private int levelId;

    private int isNewEnergy;

    // 能源类型
    private String fuelTypes;

    private int minPrice;

    private int maxPrice;

    @Data
    @AllArgsConstructor
    public static class Item{

        private int itemId;

        private int testValue;
    }

}
