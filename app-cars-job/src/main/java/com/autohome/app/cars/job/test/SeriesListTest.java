package com.autohome.app.cars.job.test;

import com.autohome.app.cars.common.redis.MainDataRedisTemplate;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.util.JedisClusterCRC16;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@JobHander("SeriesListTest")
@Service
public class SeriesListTest extends IJobHandler {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    public MainDataRedisTemplate redisTemplate;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        int times = params == null || params.length == 0 || StringUtils.isBlank(params[0]) ? 10 : Integer.parseInt(params[0]);

        List<Integer> allSeriesIds = seriesMapper.getAllSeriesIds();

        AtomicInteger slowCountG = new AtomicInteger(0);
        AtomicInteger slowCountM = new AtomicInteger(0);
        AtomicInteger slowCountP = new AtomicInteger(0);

        List<CompletableFuture> tasks = new ArrayList<>();

        //查询n次
        XxlJobLogger.log("查询车系详情，查询次数：" + times);
        for (int i = 0; i < times; i++) {
            List<Integer> seriesIds = generateRandom(allSeriesIds);


            long startMGet = System.currentTimeMillis();
            List<SeriesDetailDto> listSync = seriesDetailComponent.getListSync(seriesIds);
            long mgetTime = System.currentTimeMillis() - startMGet;
//            XxlJobLogger.log("MGET第【" + i + "】次，车系长度【" + seriesIds.size() + "】，结果长度【" + listSync.size() + "】，查询耗时：" + mgetTime + "ms");

            // GET 测试
            long startGet = System.currentTimeMillis();
            AtomicInteger count = new AtomicInteger(0);
            CompletableFuture.runAsync(() -> {
                seriesIds.parallelStream().forEach(seriesId -> {
                    SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);
                    if (seriesDetailDto != null) {
                        count.incrementAndGet();
                    }
                });
            }).join();
            long getTime = System.currentTimeMillis() - startGet;
//            XxlJobLogger.log("GET第【" + i + "】次，车系长度【" + seriesIds.size() + "】，结果长度【" + count + "】，查询耗时：" + getTime + "ms");

            //pipeline 测试

            List<String> keys = new ArrayList<>();
            seriesIds.forEach(seriesId -> {
                TreeMap param = new TreeMap();
                param.put("seriesId", seriesId);
                keys.add(seriesDetailComponent.getKey(param));
            });
            long startPipeline = System.currentTimeMillis();
            List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                keys.forEach(stringRedisConn::get);
                return null;
            });
            long pipelineTime = System.currentTimeMillis() - startPipeline;

//            XxlJobLogger.log("pipeline第【" + i + "】次，车系长度【" + seriesIds.size() + "】，结果长度【" + results.size() + "】，查询耗时：" + (System.currentTimeMillis() - startGet) + "ms");


            // 比较结果
            XxlJobLogger.log("第【" + i + "】次，车系长度【" + seriesIds.size() + "】，MGET vs GET vs Pipline，第【" + i + "】次，MGET耗时：" + mgetTime + "ms，GET耗时：" + getTime + "ms，Pipline耗时：" + pipelineTime + "ms");


            XxlJobLogger.log("=======================");
            XxlJobLogger.log("                        ");

            if (mgetTime > 50) {
                slowCountM.incrementAndGet();
            }
            if (getTime > 50) {
                slowCountG.incrementAndGet();
            }
            if (pipelineTime > 50) {
                slowCountP.incrementAndGet();
            }

        }

        XxlJobLogger.log("MGET慢查询次数：" + slowCountM.get());
        XxlJobLogger.log("GET慢查询次数：" + slowCountG.get());
        XxlJobLogger.log("Pipline慢查询次数：" + slowCountP.get());


        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }

    public List<Integer> generateRandom(List<Integer> allSeriesIds) {
        //随机获取10-80个车系ids
        Random random = new Random();
        int size = random.nextInt(60) + 10;
        List<Integer> seriesIds = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            seriesIds.add(allSeriesIds.get(random.nextInt(allSeriesIds.size())));
        }

        return seriesIds;
    }
}

