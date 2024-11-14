package com.autohome.app.cars.apiclient.share.dtos;

import lombok.Data;

@Data
public class ShareInfoResult {

    /**
     * short_url : http://athm.cn/
     * long_url : autohome://
     */

    private String short_url;
    private String long_url;

}
