package com.autohome.app.cars.apiclient.koubei.dtos;

import lombok.Data;

/**
 * @author : zzli
 * @description : 口碑新能源数据
 * @date : 2024/2/21 17:45
 */
@Data
public class SeriesInfoNewEnergyKBResult {
    /**
     * factDriveRange : 455
     * factEnergy100 : 11.9
     * officialDriveRange : 556
     */

    private int factDriveRange;
    private double factEnergy100;
    private int officialDriveRange;
    private String chargeLink;
    private String driveRangeLink;
}
