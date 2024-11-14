package com.autohome.app.cars.service.components.vr.dtos;

import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class SeriesVr {

    int seriesId;

    private SeriesVrExteriorResult vrMaterial;

    private VrH5Info h5Vr = new VrH5Info();

    private List<VrSuperCar> superCarList = new ArrayList<>();

    private VrRealScene realScene;


    @Data
    public static class VrSuperCar {
        private int browserType;
        private int exhibitionType;
        private int id;
        private String position;
        private HashMap<Integer, List<Integer>> specidinfo;
        private int terminal;
        private String title;
        private String url;
    }

    @Data
    public static class VrRealScene {
        private String show_url;
        private String series_id;
        private String cover_url;
        private String scene_url;
    }

    @NoArgsConstructor
    @Data
    public static class VrH5Info {

        private boolean hasInterior;
        private boolean hasExterior;
        private ExtInfoDTO extInfo = new ExtInfoDTO();
        private IntInfoDTO intInfo = new IntInfoDTO();

        @NoArgsConstructor
        @Data
        public static class ExtInfoDTO {
            private String showUrl;
            private String coverUrl;
            private Integer specId;
            private Boolean is_show;
            private Integer narration;
        }

        @NoArgsConstructor
        @Data
        public static class IntInfoDTO {
            private String showUrl;
            private String coverUrl;
            private Integer specId;
            private Boolean is_show;
            private Integer narration;
        }
    }
}
