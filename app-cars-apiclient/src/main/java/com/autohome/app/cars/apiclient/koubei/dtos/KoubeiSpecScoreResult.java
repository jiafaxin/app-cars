package com.autohome.app.cars.apiclient.koubei.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class KoubeiSpecScoreResult {
    private Double apperanceScore;
    private Double apperanceScoreComparedWithLevel;
    private Double avgBattery;
    private Integer avgOil;
    private Integer batteryUserNum;
    private Integer comfortablenessScore;
    private Integer comfortablenessScoreComparedWithLevel;
    private Double costEfficientScore;
    private Double costEfficientScoreComparedWithLevel;
    private Double internalScore;
    private Double internalScoreComparedWithLevel;
    private Integer isElectric;
    private Integer maneuverabilityScore;
    private Integer maneuverabilityScoreComparedWithLevel;
    private Double oilScore;
    private Integer oilUserNum;
    private Double powerScore;
    private Double powerScoreComparedWithLevel;
    private Integer scoreUserNum;
    private Double spaceScore;
    private Double spaceScoreComparedWithLevel;
    private Integer specId;
    private Double userScore;
}
