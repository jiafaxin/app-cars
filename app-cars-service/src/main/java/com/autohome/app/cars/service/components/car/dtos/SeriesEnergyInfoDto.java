package com.autohome.app.cars.service.components.car.dtos;


import com.autohome.app.cars.apiclient.vr.dtos.CockpitVrResult;
import lombok.Data;


/**
 * @author : zzli
 * @description : 新能源车系，相关的电车属性数据
 * @date : 2024/2/21 16:05
 */
@Data
public class SeriesEnergyInfoDto {

    String seriesName;
    /**
     * 是否显示新能源车系，真实续航糖豆
     */
    boolean showEVSugarBeans;
    /**
     * 超级试驾
     */

    CockpitVrResult cockpitVrResult;
    /**
     * 电动车相关
     */

    ElectricAttributes electricAttributes;

    @Data
    public static class ElectricAttributes {
        /**
         * 官方续航：CLTC>NEDC>WLTC
         */
        String enduranceMileage;
        /**
         * 电池容量
         */
        String batteryCapacity;
        /**
         * 快充时间
         */
        String fastChargetime;
        /**
         * 慢充时间
         */
        String slowChargetime;
    }
}
