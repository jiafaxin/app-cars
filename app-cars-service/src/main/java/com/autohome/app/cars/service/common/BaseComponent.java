package com.autohome.app.cars.service.common;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.common.redis.ByteMainDataRedisTemplate;
import com.autohome.app.cars.common.redis.BytePeerDataRedisTemplate;
import com.autohome.app.cars.common.redis.MainDataRedisTemplate;
import com.autohome.app.cars.common.redis.PeerDataRedisTemplate;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.services.dtos.DataSyncConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 组件基类
 *
 * @param <T>
 */
@Slf4j
public abstract class BaseComponent<T> {

    @Autowired
    public MainDataRedisTemplate redisTemplate;

    @Autowired(required = false)
    PeerDataRedisTemplate peerDataRedisTemplate;

    @Autowired
    public ByteMainDataRedisTemplate byteRedisTemplate;

    @Autowired(required = false)
    BytePeerDataRedisTemplate bytePeerDataRedisTemplate;

    @Value("${CLUSTER_NAME:}")
    String podEnvName;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.DataSyncConfig).createFromJson('${data_sync_config:}')}")
    DataSyncConfig dataSyncConfig;

    @Autowired
    DBService dbService;

    @Autowired
    SpecMapper specMapper;

    final static String RedisBaseKey = "appcar:component:";

    /**
     * 单个回源，如果需要回源的，需要再子类重写此方法
     * @param params
     * @return
     */
    protected T sourceData(TreeMap<String,Object> params){
        return null;
    }

    protected boolean gzip(){return false;}

    /**
     * 批量获取原数据,如果需要回源的，需要再子类重写此方法
     * @return
     */
    protected Map<TreeMap<String,Object>, T> sourceDatas(List<TreeMap<String,Object>> params){
        Map<TreeMap<String,Object>,T> result = new LinkedHashMap();
        for (TreeMap<String, Object> param : params) {
            result.put(param,null);
        }
        return result;
    }

    /**
     * 异步get
     * @param params
     * @return
     */
    public CompletableFuture<T> baseGetAsync(TreeMap<String,Object> params) {
        return CompletableFuture.supplyAsync(() -> baseGet(params), ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            log.error("base service get async error " + this.getClass().getSimpleName(), e);
            return null;
        });
    }

    /**
     * get
     * @param params 参数
     * @return
     */
    public T baseGet(TreeMap<String,Object> params) {
        T result = getFromRedis(params);
        if(result!=null){
            return  result;
        }
        return sourceData(params);
    }

    public T getFromRedis(TreeMap<String,Object> params) {
        String json = getFromRedis(getKey(params));
        if (StringUtils.hasLength(json)) {
            return getT(json);
        }
        return null;
    }

    /**
     * 异步批量get
     * @param params
     * @return
     */
    public CompletableFuture<List<T>> baseGetListAsync(List<TreeMap<String,Object>> params) {
        return CompletableFuture.supplyAsync(() -> baseGetList(params), ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            log.error("base service list async error " + this.getClass().getSimpleName(), e);
            return null;
        });
    }

    /**
     * 批量get
     * @param params
     * @return
     */
    public List<T> baseGetList(List<TreeMap<String,Object>> params) {
        if (params == null || params.size() == 0)
            return new ArrayList<>();
        List<String> keys = params.stream().map(x -> getKey(x)).collect(Collectors.toList());
        List<String> values = batchgetFromRedis(keys);

        List<T> results = values == null ? new ArrayList<>() : values.stream().filter(x -> StringUtils.hasLength(x)).map(x -> getT(x)).collect(Collectors.toList());
        List<TreeMap<String, Object>> noCacheParams = new ArrayList<>();
        for (int i = 0; i < params.size(); i++) {
            if (values.get(i) == null) {
                noCacheParams.add(params.get(i));
            }
        }

        if (!noCacheParams.isEmpty()) {
            //redis没数据再查库，否则查库时会有语法错误
            results.addAll(sourceDatas(noCacheParams).values());
        }
        return results;
    }

    List<String> batchgetFromRedis(List<String> keys){
        if(gzip()) {
            List<byte[]> bytesList = byteRedisTemplate.opsForValue().multiGet(keys);
            return bytesList.stream().map(x->x==null?null : new String(GZIPUtils.uncompress(x),StandardCharsets.UTF_8)).collect(Collectors.toList());
        }else{
            return redisTemplate.opsForValue().multiGet(keys);
        }
    }

    /**
     * 把参数转换为key
     * @param params
     * @return
     */
    public String getKey(TreeMap<String,Object> params) {
        String verion = null;
        RedisConfig redisConfig = this.getClass().getAnnotation(RedisConfig.class);
        if (redisConfig != null) {
            verion = redisConfig.keyVersion();
        }

        String key = params == null || params.size() == 0 ? "" : Md5Util.get(params);
        key = getBaseKey() + ":" + key;
        if (StringUtils.hasLength(verion)) {
            key = key + ":" + verion;
        }
        return key;
    }

    protected String getBaseKey(){
        return RedisBaseKey + this.getClass().getName();
    }

    /**
     * 把json转为对象
     * @param json
     * @return
     */
    private T getT(String json) {
        if (!StringUtils.hasLength(json)) {
            return null;
        }
        Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        TypeReference<T> typeReference = new TypeReference<T>() {
            @Override
            public Type getType() {
                return type;
            }
        };
        return JsonUtil.toObject(json, typeReference);
    }

    /**
     * 更新对象到reids和db中
     * @param params
     * @param data
     */
    protected void update(TreeMap<String,Object> params,T data) {
        try {
            String json = JsonUtil.toString(data);
            String oldJson = getFromRedis(getKey(params));
            //开启比较json，一致不更新数据
            if (dataSyncConfig.getCompareJsonValue() == 1 && org.apache.commons.lang3.StringUtils.equals(json, oldJson)) {
                return;
            }
            updateRedis(params, json,true);
            updateDB(params, json);
        }catch (Exception e){
            log.error("base component 更新数据异常",e);
        }
    }

    private String getFromRedis(String key){
        if(gzip()){
            byte[] bytes = byteRedisTemplate.opsForValue().get(key);
            if(bytes==null){
                return null;
            }
            return new String(GZIPUtils.uncompress(bytes),StandardCharsets.UTF_8);
        }else {
            return redisTemplate.opsForValue().get(key);
        }
    }

    private TreeMap<String,String> batchGetFromRedis(List<TreeMap<String,Object>> keyList){
        List<String> keys = keyList.stream().map(x -> getKey(x)).collect(Collectors.toList());
        List<String> values = batchgetFromRedis(keys);
        TreeMap<String,String> result = new TreeMap<>();
        for (int i = 0; i < keys.size(); i++) {
            result.put(keys.get(i),values.get(i));
        }
        return result;
    }

    /**
     * 更新对象到reids和db中
     */
    protected void updateBatch(Map<TreeMap<String,Object>,T> datas) {
        try {
            if(datas==null||datas.size()==0)
                return;

            List<TreeMap<String,Object>> keyList = datas.keySet().stream().collect(Collectors.toList());
            Map<TreeMap<String,Object>,String> saveValues = new HashMap<>();
            if (dataSyncConfig.getCompareJsonValue() == 1 ) {
                TreeMap<String,String> jsonValues = batchGetFromRedis(keyList);
                for (TreeMap<String, Object> key : keyList) {
                    String redisKey = getKey(key);
                    String newJson = JsonUtil.toString(datas.get(key));
                    if(!jsonValues.containsKey(redisKey) || !org.apache.commons.lang3.StringUtils.equals(jsonValues.get(redisKey),newJson)){
                        saveValues.put(key,newJson);
                        updateRedis(key, newJson,true);
                    }
                }
            }else{
                datas.forEach((k,v)->{
                    String newJson = JsonUtil.toString(v);
                    saveValues.put(k,newJson);
                    updateRedis(k, newJson,true) ;
                });
            }
            updateDBBatch(saveValues);
        }catch (Exception e){
            log.error("base component 更新数据异常",e);
            // 继续抛出异常方便重试
        }
    }



    protected void delete(TreeMap<String,Object> params){
        try {
            deleteRedis(params);
            deleteDB(params);
        }catch (Exception e){
            log.error("base component删除失败", e);
        }
    }

    protected void deleteRedis(TreeMap<String,Object> params) {
        redisTemplate.delete(getKey(params));
        //跨机房同步redis数据
        try {
            if (peerDataRedisTemplate != null) {
                peerDataRedisTemplate.delete(getKey(params));
            }
        } catch (Exception e) {
            log.error("peer redis delete error", e);
        }
    }

    protected void deleteRedis(List<TreeMap<String,Object>> params) {
        if(params==null || params.size()==0){
            return;
        }
        List<String> keys = new ArrayList<>();
        for (TreeMap<String, Object> param : params) {
            keys.add(getKey(param));
        }

        Lists.partition(keys,100).forEach(l-> redisTemplate.delete(l));

        //跨机房同步redis数据
        try {
            if (peerDataRedisTemplate != null) {
                Lists.partition(keys,100).forEach(l-> peerDataRedisTemplate.delete(l));
            }
        } catch (Exception e) {
            log.error("peer redis delete error", e);
        }
    }

    /**
     * 更新对象到redis
     *
     * @param params
     * @param data
     */
    @Retryable
    protected void updateRedis(TreeMap<String, Object> params, String data,boolean shuangxie) {
        int timeout = 604800;
        RedisConfig redisConfig = this.getClass().getAnnotation(RedisConfig.class);
        if (redisConfig != null) {
            timeout = redisConfig.timeout();
        }
        updateRedis(params, data, shuangxie, timeout);
    }

    protected void updateRedis(TreeMap<String, Object> params, String data,boolean shuangxie,int timeout) {
        if(gzip()){
            byte[] bytes = GZIPUtils.compress(data.getBytes(StandardCharsets.UTF_8));
            byteRedisTemplate.opsForValue().set(getKey(params), bytes, timeout, TimeUnit.SECONDS);
            if (shuangxie && bytePeerDataRedisTemplate != null && dataSyncConfig.getSyncPeer() == 1) {
                try {
                    bytePeerDataRedisTemplate.opsForValue().set(getKey(params), bytes, timeout, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("双写redis失败,请关注1 params={}, getKey={}, data={}", JsonUtil.toString(params), getKey(params), data);
                    log.error("双写redis失败1，ex:",e);
                }
            }
        }else{
            redisTemplate.opsForValue().set(getKey(params), data, timeout, TimeUnit.SECONDS);
            if (shuangxie && peerDataRedisTemplate != null && dataSyncConfig.getSyncPeer() == 1) {
                try {
                    peerDataRedisTemplate.opsForValue().set(getKey(params), data, timeout, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("双写redis失败,请关注2 params={}, getKey={}, data={}", JsonUtil.toString(params), getKey(params), data);
                    log.error("双写redis失败2，ex:",e);
                }
            }
        }
    }


    protected void updateRedisByKey(String key, String data, boolean shuangxie, int timeout, TimeUnit timeUnit) {

        redisTemplate.opsForValue().set(key, data, timeout, timeUnit);
        if (shuangxie && peerDataRedisTemplate != null && dataSyncConfig.getSyncPeer() == 1) {
            try {
                peerDataRedisTemplate.opsForValue().set(key, data, timeout, timeUnit);
            } catch (Exception e) {
                log.error("双写redis失败,请关注2 params={}, getKey={}, data={}", data, key, data);
                log.error("双写redis失败2，ex:", e);
            }
        }
    }

    protected void redis_hash_putall(String key,Map<String,String> vs){
        redisTemplate.opsForHash().putAll(key,vs);
        if (peerDataRedisTemplate != null && dataSyncConfig.getSyncPeer() == 1) {
            try {
                peerDataRedisTemplate.opsForHash().putAll(key,vs);
            } catch (Exception e) {
                log.error("双写redis失败,请关注 key="+key, e);
            }
        }
    }


//
//    /**
//     * 更新对象到redis(双活机房)
//     *
//     * @param params
//     * @param data
//     */
//    protected void updatePeerRedis(TreeMap<String, Object> params, String data) {
//        try {
//            int timeout = 10080000;
//            RedisConfig redisConfig = this.getClass().getAnnotation(RedisConfig.class);
//            if (redisConfig != null) {
//                timeout = redisConfig.timeout();
//            }
//            peerDataRedisTemplate.opsForValue().set(getKey(params), data, timeout, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            log.error("updatePeerRedis error", e);
//        }
//    }


    /**
     * 更新对象到db
     *
     * @param params
     * @param data
     */
    protected void updateDB(Map<String, Object> params, String data) {
        DBConfig dbConfig = this.getClass().getAnnotation(DBConfig.class);
        if (dbConfig == null)
            return;
        dbService.updateOrAdd(dbConfig, params, data);
    }


    protected void updateDBBatch(Map<TreeMap<String,Object>,String> datas) {
        DBConfig dbConfig = this.getClass().getAnnotation(DBConfig.class);
        if (dbConfig == null)
            return;

        dbService.addOrUpdateBatch(dbConfig, datas);
    }


    protected void deleteDB(Map<String,Object> params) {
        DBConfig dbConfig = this.getClass().getAnnotation(DBConfig.class);
        if (dbConfig == null)
            return;

        dbService.delete(dbConfig, params);
    }

    List<String> dbDefaultKeys = Arrays.asList("id","is_del","created_stime","modified_stime","data");

    protected Map<String,String> parameterMapDbField(){
        return new HashMap<>();
    }

    public void dbToRedis(Integer seconds, Consumer<String> xxlLog) {
        DBConfig dbConfig = this.getClass().getAnnotation(DBConfig.class);
        if (dbConfig == null)
            return;
        String tableName = dbConfig.tableName();
        int start = 0;
        int onceCount = 1000;
        int totalCount = 0;
        while (true) {
            List<Map<String, Object>> list = dbService.page(tableName, start, onceCount, seconds == null || seconds < 0 ? null : DateUtils.addSeconds(new Date(), -seconds));
            if (list == null || list.size() == 0) {
                break;
            }
            start = start + onceCount;
            totalCount = totalCount + list.size();
            for (Map<String, Object> map : list) {
                if (!map.containsKey("data"))
                    continue;

                TreeMap<String, Object> params = new TreeMap<>();
                String data = map.get("data").toString();
                map.forEach((k, v) -> {
                    if (dbDefaultKeys.contains(k))
                        return;
                    params.put(k, v);
                });
                if (map.get("is_del").equals(1)) {
                    deleteRedis(params);
                    continue;
                }
                updateRedis(params, data, false);
            }
        }
        xxlLog.accept("更新完成，总计：" + totalCount);
    }

    @Autowired
    protected SeriesMapper seriesMapper;

    /**
     * 循环所有车系id
     * @param totalMinutes 在多长时间内完成循环
     * @param execute 执行器，返回seriesId
     * @param log  日志输出
     */
    protected void  loopSeries(int totalMinutes, Consumer<Integer> execute,Consumer<String> log) {
        List<Integer> seriesIds = seriesMapper.getAllSeriesIds();
        int duration = totalMinutes * 60000 / seriesIds.size();
        log.accept(String.format("总计：%s 个车系，总运行时间预计：%s m，运行区间：%s ms", seriesIds.size(), totalMinutes, duration));

        long s = System.currentTimeMillis();
        int count = 1;
        for (Integer seriesId : seriesIds) {
            CompletableFuture.runAsync(()->execute.accept(seriesId),ThreadPoolUtils.defaultThreadPoolExecutor);
            ThreadUtil.sleep(duration);
            if(count++%100 ==0){
                log.accept("now:"+count);
            }
        }

        log.accept(String.format("success，总耗时：%s s", (System.currentTimeMillis() - s)/1000));
    }

    protected void  loopCity(int totalMinutes, Consumer<Integer> execute,Consumer<String> log) {
        List<Integer> cityIds = CityUtil.getAllCityIds();
        int duration = totalMinutes * 60000 / cityIds.size();
        log.accept(String.format("总计：%s 个城市，总运行时间预计：%s m，运行区间：%s ms", cityIds.size(), totalMinutes, duration));
        long s = System.currentTimeMillis();
        int count = 1;
        for (Integer cityId : CityUtil.getAllCityIds()) {
            CompletableFuture.runAsync(()->execute.accept(cityId),ThreadPoolUtils.defaultThreadPoolExecutor);
            ThreadUtil.sleep(duration);
            if(count++%100 ==0){
                log.accept("now:"+count);
            }
        }

        log.accept(String.format("success，总耗时：%s s", (System.currentTimeMillis() - s)/1000));
    }

    protected void loopSeriesCity(int totalMinutes, BiConsumer<Integer,Integer> execute, Consumer<String> log) {
        totalMinutes = totalMinutes < 60 ? 60:totalMinutes;
        List<Integer> seriesIds = seriesMapper.getAllSeriesIds();
        List<Integer> cityIds = CityUtil.getAllCityIds();

        int totalCount = seriesIds.size() * cityIds.size();

        //每秒执行个数
        int oneSecondCount = totalCount / (totalMinutes * 60);

        //单机每秒最大执行数量
        int onceCount = 50;

        int duration = 1000 / onceCount;

        //执行任务的最大节点数
        int refreshNodeCount = oneSecondCount / onceCount;

        //只允许refreshNodeCount个节点执行任务：为了控制并发量
        String nodeLockKey = getBaseKey() + ":loopSeriesCity:nodecount:lock";
        if (redisTemplate.opsForValue().increment(nodeLockKey) > refreshNodeCount) {
            redisTemplate.expire(nodeLockKey, 1, TimeUnit.MINUTES);
            log.accept("任务结束：lockcount:"+redisTemplate.opsForValue().get(nodeLockKey) + " - refreshNodeCount：" + refreshNodeCount);
            return;
        }
        redisTemplate.expire(nodeLockKey, 1, TimeUnit.MINUTES);
        String seriesLockKey = getBaseKey() + ":loopSeriesCity:seriesid:lock:";
        int count = 0;

        log.accept(String.format("最多启用 %s 个节点，预计执行 %s 分钟,每次执行间隔 %s ms",refreshNodeCount, totalMinutes,duration));

        for (Integer seriesId : seriesIds) {
            if (!redisTemplate.opsForValue().setIfAbsent(seriesLockKey + seriesId, "true", totalMinutes-10, TimeUnit.MINUTES)) {
                continue;
            }
            for (Integer cityId : CityUtil.getAllCityIds()) {
                execute.accept(seriesId, cityId);
                ThreadUtil.sleep(duration);
                if (count++ % 10000 == 0) {
                    log.accept(String.format("now:%s，SeriesId:%s，CityId:%s", count, seriesId, cityId));
                }
            }
        }
    }

    /**
     * 参数builder类
     */
    public static class ParamBuilder{
        TreeMap<String,Object> params = new TreeMap<>();

        public static ParamBuilder create(){
            return new ParamBuilder();
        }

        public static ParamBuilder create(String key,Object value) {
            return create().add(key, value);
        }

        public ParamBuilder add(String key,Object value){
            params.put(key,value);
            return this;
        }

        public TreeMap<String,Object> build(){
            return params;
        }
    }

    /**
     * 循环所有车型id
     *
     * @param totalMinutes 在多长时间内完成循环
     * @param execute      执行器，返回specId
     * @param log          日志输出
     */
    protected void loopSpec(int totalMinutes, Consumer<Integer> execute, Consumer<String> log) {
        List<Integer> specIds = specMapper.getAllSpecIds();
        List<Integer> cvSpecIds = specMapper.getAllCvSpecIds();
        specIds.addAll(cvSpecIds);
        specIds = specIds.stream().distinct().collect(Collectors.toList());
        int duration = totalMinutes * 60000 / specIds.size();
        log.accept(String.format("总计：%s 个车型，总运行时间预计：%s m，运行区间：%s ms", specIds.size(), totalMinutes, duration));

        long s = System.currentTimeMillis();
        int count = 1;
        for (Integer specId : specIds) {
            CompletableFuture.runAsync(() -> execute.accept(specId), ThreadPoolUtils.defaultThreadPoolExecutor);
            ThreadUtil.sleep(duration);
            if (count++ % 100 == 0) {
                log.accept("now:" + count);
            }
        }

        log.accept(String.format("success，总耗时：%s s", (System.currentTimeMillis() - s) / 1000));
    }

    /**
     * 循环所有车型id（分片）
     *
     * @param totalMinutes 在多长时间内完成循环
     * @param execute      执行器，返回specId
     * @param log          日志输出
     */
    protected void loopShardingSpec(int totalMinutes, Consumer<Integer> execute, Consumer<String> log) {
        List<Integer> specIds = specMapper.getAllSpecIds();
        List<Integer> cvSpecIds = specMapper.getAllCvSpecIds();
        specIds.addAll(cvSpecIds);
        specIds = specIds.stream().distinct().collect(Collectors.toList());

        int totalCount = specIds.size();
        // 每秒执行个数
        int oneSecondCount = totalCount / (totalMinutes * 60);
        // 单机每秒最大执行数量
        int onceCount = 30;
        int duration = 1000 / onceCount;
        // 执行任务的最大节点数
        int refreshNodeCount = oneSecondCount / onceCount;

        // 只允许refreshNodeCount个节点执行任务：为了控制并发量
        String nodeLockKey = getBaseKey() + ":loopShardingSpec:nodeCount:lock";
        if (redisTemplate.opsForValue().increment(nodeLockKey) > refreshNodeCount) {
            return;
        }
        redisTemplate.expire(nodeLockKey, 1, TimeUnit.MINUTES);
        String specLockKey = getBaseKey() + ":loopShardingSpec:specId:lock:";
        int count = 0;

        log.accept(String.format("最多启用 %s 个节点，预计执行 %s 分钟,每次执行间隔 %s ms", refreshNodeCount, totalMinutes, duration));

        for (Integer specId : specIds) {
            if (!redisTemplate.opsForValue().setIfAbsent(specLockKey + specId, "true", totalMinutes - 10, TimeUnit.MINUTES)) {
                continue;
            }
            CompletableFuture.runAsync(() -> execute.accept(specId), ThreadPoolUtils.defaultThreadPoolExecutor);
            ThreadUtil.sleep(duration);
            if (count++ % 10000 == 0) {
                log.accept(String.format("now:%s，specId:%s", count, specId));
            }
        }
    }

    /**
     * 循环 车型+城市
     *
     * @param totalMinutes 在多长时间内完成循环
     * @param execute      执行器，返回specId+cityId
     * @param log          日志输出
     */
    protected void loopSpecCity(int totalMinutes, BiConsumer<Integer, Integer> execute, Consumer<String> log) {
        totalMinutes = Math.max(totalMinutes, 60);
        List<Integer> specIds = specMapper.getAllSpecIds();
        List<Integer> cvSpecIds = specMapper.getAllCvSpecIds();
        specIds.addAll(cvSpecIds);
        // 车型id集合做一个倒序
        specIds = specIds.stream().distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        List<Integer> cityIds = CityUtil.getAllCityIds();

        int totalCount = specIds.size() * cityIds.size();
        // 每秒执行个数
        int oneSecondCount = totalCount / (totalMinutes * 60);
        // 单机每秒最大执行数量
        int onceCount = 50;
        int duration = 1000 / onceCount;
        // 执行任务的最大节点数
        int refreshNodeCount = oneSecondCount / onceCount;

        // 只允许refreshNodeCount个节点执行任务：为了控制并发量
        String nodeLockKey = getBaseKey() + ":loopSpecCity:nodeCount:lock";
        if (redisTemplate.opsForValue().increment(nodeLockKey) > refreshNodeCount) {
            return;
        }
        redisTemplate.expire(nodeLockKey, 1, TimeUnit.MINUTES);
        String specLockKey = getBaseKey() + ":loopSeriesCity:specId:lock:";
        int count = 0;

        log.accept(String.format("最多启用 %s 个节点，预计执行 %s 分钟,每次执行间隔 %s ms", refreshNodeCount, totalMinutes, duration));

        for (Integer specId : specIds) {
            if (!redisTemplate.opsForValue().setIfAbsent(specLockKey + specId, "true", totalMinutes - 10, TimeUnit.MINUTES)) {
                continue;
            }
            for (Integer cityId : CityUtil.getAllCityIds()) {
                execute.accept(specId, cityId);
                ThreadUtil.sleep(duration);
                if (count++ % 10000 == 0) {
                    log.accept(String.format("now:%s，specId:%s，cityId:%s", count, specId, cityId));
                }
            }
        }
    }


    /**
     * 循环所有[新能源]车系id
     * @param totalMinutes 在多长时间内完成循环
     * @param execute 执行器，返回seriesId
     * @param log  日志输出
     */
    protected void  loopNewEnergySeries(int totalMinutes, List<Integer> seriesIdList, Consumer<Integer> execute,Consumer<String> log) {
        int duration = totalMinutes * 60000 / seriesIdList.size();
        log.accept(String.format("总计：%s 个车系，总运行时间预计：%s m，运行区间：%s ms", seriesIdList.size(), totalMinutes, duration));

        long s = System.currentTimeMillis();
        int count = 1;
        for (Integer seriesId : seriesIdList) {
            CompletableFuture.runAsync(() -> execute.accept(seriesId), ThreadPoolUtils.defaultThreadPoolExecutor);
            ThreadUtil.sleep(duration);
            if (count++ % 100 == 0) {
                log.accept("now:" + count);
            }
        }

        log.accept(String.format("success，总耗时：%s s", (System.currentTimeMillis() - s) / 1000));
    }

    //删除最后更新时间之前的数据
    public int deleteHistory(HashSet<String> newKeys,Consumer<String> xxlLog){
        DBConfig dbConfig = this.getClass().getAnnotation(DBConfig.class);
        if (dbConfig == null)
            return 0;
        String tableName = dbConfig.tableName();

        int deleteCount = 0;
        int start = 0;
        int count = 10000;
        while (true) {
            List<Map<String, Object>> datas = dbService.page(tableName, start,count);
            if (datas == null || datas.size() == 0) {
                break;
            }
            start = start + count;
            List<TreeMap<String,Object>> keys = new ArrayList<>();
            List<Long> ids = new ArrayList<>();
            for (Map<String, Object> map : datas) {
                if(map.containsKey("is_del")  && map.get("is_del").equals(1)){
                    continue;
                }
                TreeMap<String, Object> params = new TreeMap<>();

                map.forEach((k, v) -> {
                    if (dbDefaultKeys.contains(k)) {
                        return;
                    }
                    params.put(k, v);
                });
                if(newKeys.contains(getKey(params))){
                    continue;
                }
                keys.add(params);
                ids.add(Long.valueOf(map.get("id").toString()));
            }
            if(keys.size()>0) {
                try {
                    deleteCount++;
                    deleteRedis(keys);
                    dbService.deleteByIds(tableName, ids);
                }catch (Exception e){
                    log.error("删除失败",e);
                }
            }
        }

        xxlLog.accept(this.getClass().getSimpleName() + " 共删除历史数据："+deleteCount);

        return deleteCount;
    }

    /**
     * 在售车型+城市 遍历
     * @param totalMinutes 总耗时
     * @param execute Consumer
     * @param log xxlLog
     */
    protected void loopOnSaleSeriesCity(int totalMinutes, BiConsumer<Integer, List<Integer>> execute, Consumer<String> log) {
        totalMinutes = Math.max(totalMinutes, 240);
        List<Integer> seriesIds = seriesMapper.getAllOnSaleSeriesIds();
        List<Integer> cityIds = CityUtil.getAllCityIds();

        //执行任务的最大节点数
        int refreshNodeCount = 1;

        //只允许refreshNodeCount个节点执行任务：为了控制并发量
        String nodeLockKey = getBaseKey() + ":loopOnSaleSeriesCity:nodecount:lock";
        Long increment = redisTemplate.opsForValue().increment(nodeLockKey);
        increment = Objects.isNull(increment) ? 0 : increment;
        if (increment > refreshNodeCount) {
            redisTemplate.expire(nodeLockKey, 1, TimeUnit.MINUTES);
            log.accept("任务结束：lockcount:" + redisTemplate.opsForValue().get(nodeLockKey) + " - refreshNodeCount：" + refreshNodeCount);
            return;
        }
        redisTemplate.expire(nodeLockKey, 1, TimeUnit.MINUTES);
        String seriesLockKey = getBaseKey() + ":loopOnSaleSeriesCity:seriesid:lock:";
        int count = 0;

        log.accept(String.format("最多启用 %s 个节点，预计执行 %s 分钟", refreshNodeCount, totalMinutes));
        for (Integer seriesId : seriesIds) {
            count++;
            if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(seriesLockKey + seriesId, "true", totalMinutes - 10, TimeUnit.MINUTES))) {
                continue;
            }
            execute.accept(seriesId, cityIds);
            log.accept(String.format("sync recommend series_spec [%d / %d] SeriesId:%s", count, seriesIds.size(), seriesId));
        }
    }

    /**
     * 专门用于车系页资讯tab下-车展数据的update方法
     * @param params
     * @param data
     */
    protected void updateAutoShow(TreeMap<String,Object> params,T data) {
        try {
            String json = JSONObject.toJSONString(data);
            String oldJson = getFromRedis(getKey(params));
            //开启比较json，一致不更新数据
            if (dataSyncConfig.getCompareJsonValue() == 1 && org.apache.commons.lang3.StringUtils.equals(json, oldJson)) {
                return;
            }
            updateRedis(params, json,true);
            updateDB(params, json);
        }catch (Exception e){
            log.error("base component 更新数据异常",e);
        }
    }



}
