package com.autohome.app.cars.common.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTest {


    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Autowired
    MainDataRedisTemplate mainDataRedisTemplate;


    @Test
    public void add(){
        stringRedisTemplate.opsForValue().set("a","1");
        Assertions.assertTrue("1".equals(stringRedisTemplate.opsForValue().get("a")));


        mainDataRedisTemplate.opsForValue().set("aa","2");
        Assertions.assertTrue("2".equals(mainDataRedisTemplate.opsForValue().get("aa")));

    }

}
