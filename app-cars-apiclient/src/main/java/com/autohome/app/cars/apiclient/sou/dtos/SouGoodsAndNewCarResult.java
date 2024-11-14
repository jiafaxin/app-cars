package com.autohome.app.cars.apiclient.sou.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/6
 */
@Data
public class SouGoodsAndNewCarResult {

    private Integer cost;
    private Integer rowcount;
    private List<ListDTO> list;

    @Data
    public static class ListDTO {
        private Integer id;
        private Integer type;
        private DataDTO data;
    }

    @Data
    public static class DataDTO {
        private String cms_brand_ids;
        private String cms_brand_names;
        private String cms_series_ids;
        private String cms_series_names;
        private String cms_spec_ids;
        private String cms_spec_names;
    }

}
