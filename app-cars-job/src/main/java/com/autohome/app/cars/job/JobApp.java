package com.autohome.app.cars.job;

import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.job.common.JobTestTool;
import com.autohome.job.core.util.HandlerTestTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.EnableRetry;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.CRC32;

@EnableDubbo
@SpringBootApplication(scanBasePackages = "com.autohome.app.cars")
@Slf4j
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
@EnableRetry
public class JobApp {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(JobApp.class, args);
        if (args != null && Arrays.stream(args).anyMatch(x -> x.indexOf("jobhandlertest") >= 0)) {
            JobTestTool.Testing(applicationContext);
        }
    }


}
