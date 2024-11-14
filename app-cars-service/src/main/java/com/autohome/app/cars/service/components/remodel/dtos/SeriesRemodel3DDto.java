package com.autohome.app.cars.service.components.remodel.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SeriesRemodel3DDto {
    private Integer status;
    private String seriesId;
    private Integer modelId;
    private String projectUid;
    private String jumpUrl;
    private String jumpProjectUrl;
    private String jumpPaintingUrl;
    private List<String> smallImages;
    private List<String> images;
}
