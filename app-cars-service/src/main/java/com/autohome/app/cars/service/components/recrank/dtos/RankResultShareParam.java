package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 榜单结果中的
 */
@Data
@NoArgsConstructor
public class RankResultShareParam {
    private String date;
    private Integer typeid;
    private Integer subranktypeid;
    private Integer seriesid;
    private Integer cityid;

}