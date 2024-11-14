package com.autohome.app.cars.apiclient;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.autohome.**"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class ApiClientTestApp {
    public static void main(String[] args) {
        SpringApplication.run(ApiClientTestApp.class, args);
    }
}
