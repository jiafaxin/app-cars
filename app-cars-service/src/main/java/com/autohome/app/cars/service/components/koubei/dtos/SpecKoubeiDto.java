package com.autohome.app.cars.service.components.koubei.dtos;

import lombok.Data;

@Data
public class SpecKoubeiDto {

    /**
     * 口碑评分
     */
    private double userScore;

    /**
     * 口碑人数
     */
    private int scoreUserNum;

}
