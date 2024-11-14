package com.autohome.app.cars.apiclient.vr.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : zzli
 * @description : 超级测试
 * @date : 2024/2/21 16:25
 */
@Data
public class CockpitVrResult {
    private String title;
    private Integer series_id;
    private String series_name;
    private Integer spec_id;
    private String spec_name;
    private String show_url;
}
