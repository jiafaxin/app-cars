package com.autohome.app.cars.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.autohome.**"})
public class CommonTestApp {
    public static void main(String[] args) {
        SpringApplication.run(CommonTestApp.class, args);
    }
}
