package com.autohome.app.cars.apiclient;

import com.autohome.app.cars.apiclient.car.CarApiClient;
import com.autohome.app.cars.common.utils.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CarApiTest {

    @Autowired
    CarApiClient carApiClient;

    @Test
    public void Brand_GetBrandLogo() {
        carApiClient.brandGetBrandLogo(1,"cookie","header").thenAccept(result->{
            System.out.println(JsonUtil.toString(result));
            Assertions.assertTrue(result != null && result.getReturncode() == 0);
        }).join();
    }

}