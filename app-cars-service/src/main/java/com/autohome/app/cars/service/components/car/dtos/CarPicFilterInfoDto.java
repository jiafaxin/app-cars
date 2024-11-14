package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CarPicFilterInfoDto {
    private List<FilterBean> piccolor1_a;
    private List<FilterBean> piccolor1_b;
    private List<FilterBean> piccolor2_a;
    private List<FilterBean> piccolor2_b;
    private List<FilterBean> piccolor3_a;
    private List<FilterBean> piccolor3_b;

    @Data
    public static class FilterBean {
        /**
         * seriesId : 18
         * specInfos : [{"specId":58034,"outColors":[3224,6774],"innerColors":[3224,6774]}]
         */

        private int seriesId;
        private List<SpecInfosBean> specInfos;

        @Data
        public static class SpecInfosBean {
            /**
             * specId : 58034
             * outColors : [3224,6774]
             * innerColors : [3224,6774]
             */

            private int specId;
            private List<Integer> outColors;
            private List<Integer> innerColors;
        }
    }

//    private int seriesId;
//    private List<SpecInfoFilter> specInfos;
//
//    @Data
//    public static class SpecInfoFilter{
//        private int specId;
//        private List<Integer> outColors;
//        private List<Integer> innerColors;
//    }

}
