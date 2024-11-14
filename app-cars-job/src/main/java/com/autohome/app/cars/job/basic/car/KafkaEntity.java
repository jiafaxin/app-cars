package com.autohome.app.cars.job.basic.car;

import lombok.Data;

@Data
public class KafkaEntity {
    private String business;

    private String type;

    private String action;

    private KafkaDataEntity data;

    @Data
    public static class KafkaDataEntity {
        private int id;

        private int brandId;

        private int seriesId;

        private int specId;
    }
}
