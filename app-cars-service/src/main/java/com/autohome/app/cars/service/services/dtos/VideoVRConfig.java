package com.autohome.app.cars.service.services.dtos;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class VideoVRConfig {

    private static final Logger logger = LoggerFactory.getLogger(VideoVRConfig.class);

    private int id;
    private int seriesid;
    private int specid;
    private int colorid;

    private int remotecolorid;

    private String colorvalue;
    private boolean enable;
    private ActionvideoinfoDTO actionvideoinfo;
    private String pointvideoname;

    @Data
    public static class ActionvideoinfoDTO {
        private String seriesname;
        private String videourl;
        private int videobytesize;
        private String videoimage;
        private VideoinfoDTO fullvideoinfo;

        private VideoinfoDTO verticalfullvideoinfo;

        private Integer videowidth;
        private Integer videoheight;

        @Data
        public static class VideoinfoDTO {
            private String videourl;
            private int videobytesize;
            private String videoimage;
        }


    }

    public static List<VideoVRConfig> decodeVideoVRConfig(String json) {
        try {
            if (json == null || json.equals("")) {
                return new ArrayList<>();
            }
            return JSON.parseObject(json, new TypeReference<List<VideoVRConfig>>(){});
        } catch (Exception e) {
            logger.error("VideoVRConfig 序列化失败");
            return new ArrayList<>();
        }
    }
}
