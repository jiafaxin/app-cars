package com.autohome.app.cars.job.test;

import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@JobHander("RedisClusterTest")
@Service
public class RedisClusterTest  extends IJobHandler {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {

//        String b100 = generateRandomString(100);
//        String k1 = generateRandomString(1024);
//        String k10 = generateRandomString(10240);
//        String k100 = generateRandomString(102400);

        exec(100,10,100000);
        exec(1024,10,100000);
        exec(10240,10,100000);
        exec(102400,10,100000);

        exec(100,10,1000000);
        exec(1024,10,1000000);
        exec(10240,10,1000000);
        exec(102400,10,1000000);


        exec(100,100,100000);
        exec(1024,100,100000);
        exec(10240,100,100000);
        exec(102400,100,100000);

        exec(100,100,1000000);
        exec(1024,100,1000000);
        exec(10240,100,1000000);
        exec(102400,100,1000000);


        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }

    public void exec(int bc,int threadCount,int runCount) {
        String str = generateRandomString(bc);
        long s = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < runCount; i++) {
            int finalI = i;
            CompletableFuture.runAsync(() -> stringRedisTemplate.opsForValue().set("b" + bc + ":" + finalI, str), executor);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            XxlJobLogger.log(bc + "字节 " + threadCount + "线程 "+runCount+"次set 用时：" + (System.currentTimeMillis() - s)+"ms");
        }catch (Exception e){

        }
    }

    public static String generateRandomString(int byteSize) {
        int characterSize = (int) Math.ceil((double) byteSize / 2);
        byte[] randomBytes = new byte[characterSize];
        new Random().nextBytes(randomBytes);
        return new String(randomBytes, StandardCharsets.UTF_8);
    }
}
