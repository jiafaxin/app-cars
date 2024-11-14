package com.autohome.app.cars.apiclient.car.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 口碑接口数据传输对象
 * 口碑接口文档：https://zhishi.autohome.com.cn/home/teamplace/file?targetId=yR4epSKuci
 *
 * @author chenqixin
 */
@NoArgsConstructor
@Data
public class KouBeiInfoDto {

    private String appScheme;
    private Integer appendId;
    private Integer carConfigContentId;
    private String configFullId;
    private Integer configId;
    private String created;
    private String feelingSummary;
    private Integer grade;
    private Integer koubeiId;
    private String longContent;
    private Integer rankOrder;
    private Integer sceneId;
    private Integer seriesDetailKey;
    private Integer seriesId;
    private String seriesName;
    private String shortContent;
    private Integer specId;
    private Integer subConfigId;
    private Integer syear;
    private String userId;
}
