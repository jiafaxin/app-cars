package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ListAreaContentResult {

    /**
     * btnType : 2
     * mainText : 查落地价
     * subText :
     * telNO : null
     * cornerText :
     * windowType : 21
     * formTitle : 获取底价
     * formLabel : []
     * formMainText : 获取底价
     * formSubText : 一键询全城
     * dealerId : null
     * specId : 65272
     * areaId : 5
     * ext : null
     * url : null
     */

    private int btnType;
    private String mainText;
    private String subText;
    private Object telNO;
    private String cornerText;
    private int windowType;
    private String formTitle;
    private String formMainText;
    private String formSubText;
    private Object dealerId;
    private int specId;
    private int areaId;
    private Object ext;
    private Object url;
    private List<?> formLabel;

}
