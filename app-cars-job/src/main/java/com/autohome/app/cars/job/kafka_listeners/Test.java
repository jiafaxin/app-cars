package com.autohome.app.cars.job.kafka_listeners;

import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@JobHander("KafkaTest")
public class Test extends IJobHandler {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        kafkaTemplate.send("autohome_data_transit", "kafkatest");
        //
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }


//    @KafkaListener(id = "listen1", topics = "", containerFactory = "kafkaDefaultContainerFactory")
//    public void listen1(String in) {
//        System.out.println("接收到结果"+in);
//    }
}
