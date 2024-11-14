package com.autohome.app.cars.service.components.remodel.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class SeriesRemodelCoversDto {

    private Integer total;
    private List<ListDTO> list = new ArrayList<>();
    private Integer pageIndex = 1;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        private String spec_name = "";
        private String app_url = "";
        private String img_url = "";
        private int biz_id;
        private int community_width;
        private int spec_id;
        private int community_height;
    }
}
