package com.autohome.app.cars.service.components.misc.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchengtao
 * @date 2024/8/22 14:27
 */
@NoArgsConstructor
@Data
public class NewSeriesCityHotNewsAndTabDto {
    private String icon;
    private Integer sort;
    private String title;
    private String type;
    private Integer seriesId;
    private Integer cityId;
    private String subtitle;
    private String linkUrl;
    private Integer position;
    private String redDot;
    private Integer pageCardDataId;
}
