package com.autohome.app.cars.apiclient.bfai.dtos;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SeriesSortParam {

    private String rid;
    private String device_id;
    private String device_type;
    private String uid;
    private String source;
    private String net_state;
    private Boolean is_debug;
    private Integer city_id;
    private Integer brand_id;
    private String brand_name;
    private List<SeriesDTO> series =new ArrayList<>();
    private Integer need_img_url;
    private String from;
    @Data
    public static class SeriesDTO {
        private Integer series_id;
        private Integer status;
    }
}
