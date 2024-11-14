package com.autohome.app.cars.apiclient.club.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchengtao
 * @date 2024/8/21 20:27
 */
@NoArgsConstructor
@Data
public class SeriesBbsResult {

    private Integer inputbbsid;
    private Integer bbsid;
    private String bbs;
    private String bbsname;
    private String firstletter;
    private Integer bbsstate;
    private String bbsimgurl;
    private Object backgroundImg;
    private Integer isnewenergy;
    private Integer seriesid;
}
