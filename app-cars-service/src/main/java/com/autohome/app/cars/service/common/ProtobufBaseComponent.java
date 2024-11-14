package com.autohome.app.cars.service.common;

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
import com.google.protobuf.GeneratedMessageV3;
import io.lettuce.core.cluster.SlotHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
public abstract class ProtobufBaseComponent<T extends GeneratedMessageV3> {

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
     *
     * @param params
     * @return
     */
    protected T sourceData(TreeMap<String, Object> params) { return null;
    }

    /**
     * 批量获取原数据,如果需要回源的，需要再子类重写此方法
     *
     * @return
     */
    protected Map<TreeMap<String, Object>, T> sourceDatas(List<TreeMap<String, Object>> params) {
        Map<TreeMap<String, Object>, T> result = new LinkedHashMap();
        for (TreeMap<String, Object> param : params) {
            result.put(param, null);
        }
        return result;
    }

    /**
     * 异步get
     *
     * @param params
     * @return
     */
    public CompletableFuture<T> baseGetAsync(TreeMap<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> baseGet(params), ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            log.error("base service get async error " + this.getClass().getSimpleName(), e);
            return null;
        });
    }

    /**
     * get
     *
     * @param params 参数
     * @return
     */
    public T baseGet(TreeMap<String, Object> params) {
        T result = getFromRedis(params);
        if (result != null) {
            return result;
        }
        return sourceData(params);
    }

    public T getFromRedis(TreeMap<String, Object> params) {
        byte[] bytes = getFromRedis(getKey(params));
        if (bytes != null) {
            return getT(bytes);
        }
        return null;
    }

    /**
     * 异步批量get
     *
     * @param params
     * @return
     */
    public CompletableFuture<List<T>> baseGetListAsync(List<TreeMap<String, Object>> params) {
        RedisConfig redisConfig = this.getClass().getAnnotation(RedisConfig.class);
        if (redisConfig != null && redisConfig.useCustomSlot()) {
            return baseGetListCustomSlot(params);
        }
        return CompletableFuture.supplyAsync(() -> baseGetList(params), ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            log.error("base service list async error " + this.getClass().getSimpleName(), e);
            return null;
        });
    }

    /**
     * 批量get
     *
     * @param params
     * @return
     */
    public List<T> baseGetList(List<TreeMap<String, Object>> params) {
        if (params == null || params.size() == 0)
            return new ArrayList<>();
        List<String> keys = params.stream().map(x -> getKey(x)).collect(Collectors.toList());
        List<byte[]> values = batchgetFromRedis(keys);

        List<T> results = values == null ? new ArrayList<>() : values.stream().map(x -> x == null ? null : getT(x)).collect(Collectors.toList());
        List<TreeMap<String, Object>> noCacheParams = new ArrayList<>();
        for (int i = 0; i < params.size(); i++) {
            if (values.get(i) == null) {
                noCacheParams.add(params.get(i));
            }
        }
        results.addAll(sourceDatas(noCacheParams).values());
        return results;
    }


    public CompletableFuture<List<T>> baseGetListCustomSlot(List<TreeMap<String, Object>> params) {
        if (params == null || params.size() == 0)
            return CompletableFuture.completedFuture(new ArrayList<>());
        List<String> keys = params.stream().map(x -> getKey(x)).collect(Collectors.toList());
        //key 分slot
        Map<Integer, List<String>> slotKeys = new HashMap<>();
        for (String key : keys) {
            int slot = SlotHash.getSlot(key);
            if (!slotKeys.containsKey(slot)) {
                slotKeys.put(slot, new ArrayList<>());
            }
            slotKeys.get(slot).add(key);
        }

        //每个slot的key用一个单独的线程处理
        ConcurrentMap<String, T> mapResult = new ConcurrentHashMap<>();
        List<CompletableFuture> tasks = new ArrayList<>();
        slotKeys.forEach((slot, keyList) -> {
            tasks.add(
                    CompletableFuture.runAsync(() -> {
                        List<T> datas;
                        List<byte[]> jsonValues = batchgetFromRedis(keyList);
                        datas = getTs(jsonValues);
                        for (int i = 0; i < keyList.size(); i++) {
                            T data = datas.get(i);
                            if (data == null) {
                                continue;
                            }
                            mapResult.put(keyList.get(i), data);
                        }
                    }, ThreadPoolUtils.defaultThreadPoolExecutor)
            );
        });

        //线程执行完毕后，组装数据
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).thenApply(x -> {
            List<T> results = new ArrayList<>();
            for (String key : keys) {
                if (!mapResult.containsKey(key)) {
                    continue;
                }
                results.add(mapResult.get(key));
            }
            return results;
        });

    }

    List<byte[]> batchgetFromRedis(List<String> keys) {
        return byteRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 把参数转换为key
     *
     * @param params
     * @return
     */
    public String getKey(TreeMap<String, Object> params) {
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

        if (redisConfig != null && redisConfig.useCustomSlot()) {
            return RedisKeyUtil.convertToCustomSlotKey(key);
        }

        return key;
    }

    protected String getBaseKey() {
        return RedisBaseKey + this.getClass().getName();
    }

    /**
     * 把json转为对象
     *
     * @param data
     * @return
     */
    private T getT(byte[] data) {
        if (data == null) {
            return null;
        }
        Class clazz = ((Class) (((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]));
        try {
            Method parseFromMethod = clazz.getMethod("parseFrom", byte[].class);
            return data == null ? null : (T) parseFromMethod.invoke(null, data);
        } catch (Exception e) {
            log.error("转换失败", e);
            return null;
        }
    }

    /**
     * 批量转对象
     *
     * @param
     * @return
     */
    private List<T> getTs(List<byte[]> datas) {
        if (datas == null || datas.size() == 0) {
            return new ArrayList<>();
        }
        Class clazz = ((Class) (((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]));
        try {
            Method parseFromMethod = clazz.getMethod("parseFrom", byte[].class);
            return datas.stream().map(data -> {
                try {
                    return data == null ? null : (T) parseFromMethod.invoke(null, data);
                } catch (Exception e) {
                    log.error("转换失败", e);
                    return null;
                }
            }).collect(Collectors.toList());
        } catch (NoSuchMethodException e) {
            log.error("非message", e);
            return new ArrayList<>();
        }

    }

    /**
     * 更新对象到reids和db中
     *
     * @param params
     * @param data
     */
    protected void update(TreeMap<String, Object> params, T data) {
        try {
            byte[] bytes = data.toByteArray();
            byte[] oldbytes = getFromRedis(getKey(params));
            //开启比较json，一致不更新数据
            if (dataSyncConfig.getCompareJsonValue() == 1 && Arrays.equals(bytes, oldbytes)) {
                return;
            }
            updateRedis(params, bytes, true);
        } catch (Exception e) {
            log.error("base component 更新数据异常", e);
        }
    }

    private byte[] getFromRedis(String key) {
        return byteRedisTemplate.opsForValue().get(key);
    }

    protected void delete(TreeMap<String, Object> params) {
        try {
            deleteRedis(params);
        } catch (Exception e) {
            log.error("base component删除失败", e);
        }
    }

    protected void deleteRedis(TreeMap<String, Object> params) {
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

    protected void deleteRedis(List<TreeMap<String, Object>> params) {
        if (params == null || params.size() == 0) {
            return;
        }
        List<String> keys = new ArrayList<>();
        for (TreeMap<String, Object> param : params) {
            keys.add(getKey(param));
        }

        Lists.partition(keys, 100).forEach(l -> redisTemplate.delete(l));

        //跨机房同步redis数据
        try {
            if (peerDataRedisTemplate != null) {
                Lists.partition(keys, 100).forEach(l -> peerDataRedisTemplate.delete(l));
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
    protected void updateRedis(TreeMap<String, Object> params, byte[] data, boolean shuangxie) {
        int timeout = 604800;
        RedisConfig redisConfig = this.getClass().getAnnotation(RedisConfig.class);
        if (redisConfig != null) {
            timeout = redisConfig.timeout();
        }
        updateRedis(params, data, shuangxie, timeout);
    }

    protected void updateRedis(TreeMap<String, Object> params, byte[] data, boolean shuangxie, int timeout) {
        byteRedisTemplate.opsForValue().set(getKey(params), data, timeout, TimeUnit.SECONDS);
        if (shuangxie && bytePeerDataRedisTemplate != null && dataSyncConfig.getSyncPeer() == 1) {
            try {
                bytePeerDataRedisTemplate.opsForValue().set(getKey(params), data, timeout, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("双写redis失败,请关注1 params={}, getKey={}, data={}", JsonUtil.toString(params), getKey(params), data);
                log.error("双写redis失败1，ex:", e);
            }
        }
    }

    /**
     * 参数builder类
     */
    public static class ParamBuilder {
        TreeMap<String, Object> params = new TreeMap<>();

        public static ParamBuilder create() {
            return new ParamBuilder();
        }

        public static ParamBuilder create(String key, Object value) {
            return create().add(key, value);
        }

        public ParamBuilder add(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public TreeMap<String, Object> build() {
            return params;
        }
    }
}
