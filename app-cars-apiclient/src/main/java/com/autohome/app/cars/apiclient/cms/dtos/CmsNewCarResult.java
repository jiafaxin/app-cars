package com.autohome.app.cars.apiclient.cms.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author : zzli
 * @description : 新车上市(资讯)
 * @date : 2024/3/27 13:55
 */
@NoArgsConstructor
@Data
public class CmsNewCarResult {

    private Integer id;

    private String url;

    private String smallTitle;
    /**
     * 上市时间戳
     */
    private Long onTime;

    private String onTimeNote;
    private String img;
    private Boolean onmarket;
    /**
     * 全新车系 1
     * 车系换代 2
     * 中期改款 5
     * 小改款 6
     * 新加车型 4
     */

    private Integer tagId;

    private String tagName;

    public Date getOnDate() {
        return getOnTime() != null ? new Date(getOnTime()) : null;
    }
}
