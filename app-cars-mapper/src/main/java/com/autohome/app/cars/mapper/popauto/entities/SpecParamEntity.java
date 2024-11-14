package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author : zzli
 * @description : 车型参配
 * @date : 2024/2/26 14:29
 */
@Data
public class SpecParamEntity {
    int seriesId;
    int specId;
    int specState;
    String seats;//座位数
    int paramId;
    String paramName;
    String paramValue;
    String subParamId;
    String subParamName;
    private int ParamSort;//排序
    String logo;
    private Timestamp Modified_Stime;
}
