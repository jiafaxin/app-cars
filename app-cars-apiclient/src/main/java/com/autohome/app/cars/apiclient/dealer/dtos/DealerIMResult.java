package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

/**
 * @author : zzli
 * @description : IM
 * @date : 2024/4/23 20:18
 */
@Data
public class DealerIMResult {

    private String title;
    private String btnname;
    private String linkurl;
    private String icon;
    private String h5url;
    private Integer localdealercount;
    private String sourceId;
}
