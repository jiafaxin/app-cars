package com.autohome.app.cars.apiclient.cms.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CmsTestEvalItemsResult {

    private EvaluatedtoBean evaluatedto;

    @Data
    public static class EvaluatedtoBean {
        private List<EvaluateitemsBean> evaluateitems;

        @Data
        public static class EvaluateitemsBean {
            private String categoryname;
            private double data;
        }
    }
}
