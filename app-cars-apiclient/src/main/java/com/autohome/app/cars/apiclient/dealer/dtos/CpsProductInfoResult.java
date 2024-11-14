package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpsProductInfoResult {

    private Integer seriesId;

    private Integer skuId;

    private Integer editionId;

    private String imageUrl;

    private String jumpUrl;

    private Integer isCpsSku;

    private String clickaction;

    private String showaction;

    private String extvalue;

    private List<String> adTxtList;

    private Long endDate;

    private Integer orderNum;
    private Integer hasCPSData;
    private Integer siteId;
    private Integer adType;
}
