package com.autohome.app.cars.apiclient.vr.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SpecVrInfoResult {

    private boolean HasInterior;
    private boolean HasExterior;
    private List<SNewsSeriesVrInfo_IntInfo> IntInfo;
    private List<SNewsSeriesVrInfo_ExtInfo> ExtInfo;
    private String QueryTime;

    @Data
    public static class SNewsSeriesVrInfo_IntInfo {
        private int PanoId;
        private String ShowUrl;
        private String CoverUrl;
        private int VisitCount;
        private int LikeCount;
        private int SceneCount;
        private int HotspotCount;
        private int SeriesId;
        private int SpecId;
        private Boolean is_show;
        private int narration;
    }

    @Data
    public static class SNewsSeriesVrInfo_ExtInfo {
        private int ExtId;
        private String ShowUrl;
        private String CoverUrl;
        private int VisitCount;
        private int LikeCount;
        private int ColorCount;
        private int FrameCount;
        private int HotspotCount;
        private int SeriesId;
        private int SpecId;
        private Boolean is_show;
        private String[] BaseColorList;
        private String[] ColorList;
        private int narration;
    }

}
