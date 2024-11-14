package com.autohome.app.cars.common.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class KafkaTest {

    @Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;

    @Test
    public void send(){
        kafkaTemplate.send("kafka_data_topic","test");
    }

    @KafkaListener(id = "listen1", topics = "kafka_data_topic")
    public void listen1(String in) {
        System.out.println(in);
    }

}
