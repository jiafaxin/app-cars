package com.autohome.app.cars.apiclient.remodel.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class RemodelCoversResult {

    private Integer total;
    private List<ListDTO> list = new ArrayList<>();

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        private List<String> spec_names;
        private String app_url;
        private String img_url;
        private String main_data_type;
        private String title;
        private Integer biz_id;
        private Integer community_width;
        private List<Integer> series_ids;
        private List<Integer> spec_ids;
        private Integer is_top;
        private Integer community_height;
    }
}
