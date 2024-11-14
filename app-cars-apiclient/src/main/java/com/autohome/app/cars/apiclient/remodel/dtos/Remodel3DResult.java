package com.autohome.app.cars.apiclient.remodel.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Remodel3DResult {
    private Integer status;
    private String seriesId;
    private Integer modelId;
    private String projectUid;
    private String jumpUrl;
    private String jumpProjectUrl;
    private String jumpPaintingUrl;
    private List<String> smallImages;
    private List<String> images;
    private String mJumpUrl;
    private String mJumpProjectUrl;
    private String mJumpPaintingUrl;
}
