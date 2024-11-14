package com.autohome.app.cars.service.components.vr.dtos;

import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import lombok.Data;

import java.util.List;

@Data
public class SpecVrInfoDto {

    private boolean HasExterior;
    private List<SNewsSeriesVrInfo_IntInfo> IntInfo;
    private List<SNewsSeriesVrInfo_ExtInfo> ExtInfo;

    private SeriesVrExteriorResult vrMaterial;

    @Data
    public static class SNewsSeriesVrInfo_IntInfo {
        private String CoverUrl;
        private String ShowUrl;
        private Boolean is_show;
        private Integer narration;
    }

    @Data
    public static class SNewsSeriesVrInfo_ExtInfo {
        private String CoverUrl;
        private Boolean is_show;
        private Integer narration;
    }

}
