package com.autohome.app.cars.service.components.vr.dtos;

import lombok.Data;

@Data
public class SeriesVrPointDto {
    /**
     * 60图的排位id对应表：Car_Sixty_Point_Item的字段Ordercls;
     */
    private int classid;
    /**
     * 60图点位名称
     */
    private String name;

    /**
     * VR 点位id
     */
    private String frameids;

    /**
     * 图片分类id
     */
    private int picclassid;
}
