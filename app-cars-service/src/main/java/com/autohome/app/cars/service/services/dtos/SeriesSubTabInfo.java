package com.autohome.app.cars.service.services.dtos;

import lombok.Data;

/**
 * 车系页tab
 */
@Data
public class SeriesSubTabInfo {

    public SeriesSubTabInfo() {
    }

    public SeriesSubTabInfo(String cardTitle, int typeId) {
        this.setCardtitle(cardTitle);
        this.setTypeid(typeId);
    }

    private String tabtitle = "";
    private int typeid;
    private String taburl = "";
    private String cardtitle = "";
    private String pluginversion = "";
    private Integer isshowrecommendcar;

    private Integer moduletype=0;
    private Integer displaytitlebar=0;
    private String tabicon="";

}
