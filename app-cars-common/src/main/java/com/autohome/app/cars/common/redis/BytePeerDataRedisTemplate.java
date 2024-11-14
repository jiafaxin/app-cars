package com.autohome.app.cars.common.redis;

import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

public class BytePeerDataRedisTemplate extends RedisTemplate<String, byte[]> {

    public BytePeerDataRedisTemplate(RedisConnectionFactory connectionFactory) {
        super();
        setKeySerializer(RedisSerializer.string());
        setValueSerializer(RedisSerializer.byteArray());
        setHashKeySerializer(RedisSerializer.string());
        setHashValueSerializer(RedisSerializer.byteArray());
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

    protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
        return new DefaultStringRedisConnection(connection);
    }
}
