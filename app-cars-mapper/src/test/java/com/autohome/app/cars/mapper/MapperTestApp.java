package com.autohome.app.cars.mapper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.autohome.**"})
@MapperScan("com.autohome.app.cars.mapper.popauto")
public class MapperTestApp {
    public static void main(String[] args) {
        SpringApplication.run(MapperTestApp.class, args);
    }
}
