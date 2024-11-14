package com.autohome.app.cars.common.kafka;

import lombok.Data;

@Data
public class KafkaProperties {
    String bootstrapServers;
    String clientId;
    Consumer consumer;

    @Data
    public static class Consumer{
        String groupId;
    }

}
