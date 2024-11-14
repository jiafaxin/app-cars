package com.autohome.app.cars.provider.controller;

import com.alibaba.fastjson2.JSON;
import com.autohome.app.cars.service.services.dtos.AutoShowConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
public class HomeController {
    @GetMapping(value = {"/index", "/"})
    public String hello() {
        return "success";
    }

    @Value("#{T(com.autohome.app.cars.service.services.dtos.AutoShowConfig).decodeAutoShowConfig('${autoshow_config:}')}")
    AutoShowConfig autoShowConfig;

    @RequestMapping(value = {"/prestop"}, method = RequestMethod.GET, produces = "application/json")
    public void prestop() {
        try {
            Thread.currentThread().sleep(1000 * 300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.error("### pod prestop at " + new Date());
    }

    @GetMapping("/log")
    public String index() {
        log.info("info");
        log.warn("warn");
        log.error("error","1");
        return "success";
    }

    @GetMapping("/test")
    public String test() {
        return JSON.toJSONString(autoShowConfig);
    }

    @GetMapping("/testCdn")
    public String testCdn() {
        // 返回当前时间yyyy-MM-dd HH:mm:ss
        return new Date().toString();
    }
}
