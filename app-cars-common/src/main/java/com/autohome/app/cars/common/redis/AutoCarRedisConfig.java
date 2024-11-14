package com.autohome.app.cars.common.redis;

import io.lettuce.core.ClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
@Slf4j
public class AutoCarRedisConfig {

    /**
     * 默认的redistemplate
     */
    @Configuration
    static class Default {

        @Bean
        @ConfigurationProperties(prefix = "spring.redis.default")
        ClusterConfigurationProperties defaultRedisProperties() {
            return new ClusterConfigurationProperties();
        }

        @Bean
        public RedisConnectionFactory redisConnectionFactory(@Qualifier("defaultRedisProperties") ClusterConfigurationProperties properties) {
            return createLettuceConnectFactory(properties);
        }

        @Bean
        @Primary
        public StringRedisTemplate redisTemplate(@Qualifier("redisConnectionFactory")
                                                 RedisConnectionFactory primaryConnectionFactory) {
            return new StringRedisTemplate(primaryConnectionFactory);
        }
    }

    /**
     * 只保存主数据
     */
    @Configuration
    static class MainData {

        @Bean
        @ConfigurationProperties(prefix = "spring.redis.main-data")
        ClusterConfigurationProperties mainDataRedisProperties() {
            return new ClusterConfigurationProperties();
        }

        @Bean
        public RedisConnectionFactory mainDataConnectionFactory(@Qualifier("mainDataRedisProperties") ClusterConfigurationProperties properties) {
            return createLettuceConnectFactory(properties);
        }

        @Bean
        public MainDataRedisTemplate mainDataRedisTemplate(@Qualifier("mainDataConnectionFactory")
                                                           RedisConnectionFactory primaryConnectionFactory) {
            return new MainDataRedisTemplate(primaryConnectionFactory);
        }


        @Bean
        public ByteMainDataRedisTemplate byteMainDataRedisTemplate(@Qualifier("mainDataConnectionFactory")
                                                           RedisConnectionFactory primaryConnectionFactory) {
            return new ByteMainDataRedisTemplate(primaryConnectionFactory);
        }
    }

    /**
     * 双机房对等的redis
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.redis.peer-main-data.cluster.nodes[0]")
    static class PeerMainData {

        @Bean
        @ConfigurationProperties(prefix = "spring.redis.peer-main-data")
        ClusterConfigurationProperties peerMainDataRedisProperties() {
            return new ClusterConfigurationProperties();
        }

        @Bean
        public RedisConnectionFactory peerMainDataConnectionFactory(@Qualifier("peerMainDataRedisProperties") ClusterConfigurationProperties properties) {
            return createLettuceConnectFactory(properties);
        }

        @Bean
        public PeerDataRedisTemplate peerMainDataRedisTemplate(@Qualifier("peerMainDataConnectionFactory")
                                                               RedisConnectionFactory primaryConnectionFactory) {
            return new PeerDataRedisTemplate(primaryConnectionFactory);
        }

        @Bean
        public BytePeerDataRedisTemplate bytePeerMainDataRedisTemplate(@Qualifier("peerMainDataConnectionFactory")
                                                               RedisConnectionFactory primaryConnectionFactory) {
            return new BytePeerDataRedisTemplate(primaryConnectionFactory);
        }
    }

//    static RedisConnectionFactory createConnectFactory(ClusterConfigurationProperties redisInfo) {
//        Assert.isTrue(redisInfo != null && redisInfo.getCluster() != null && redisInfo.getCluster().getNodes() != null && redisInfo.getCluster().getNodes().size() > 0, "请先配置redis");
//
//        RedisClusterConfiguration config = new RedisClusterConfiguration(redisInfo.getCluster().getNodes());
//        if (StringUtils.hasLength(redisInfo.password)) {
//            config.setPassword(redisInfo.password);
//        }
//        config.setMaxRedirects(redisInfo.getMaxRedirects());
//
//
//
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(redisInfo.getPool().getMaxActive());
//        poolConfig.setMaxIdle(redisInfo.getPool().getMaxIdle());
//        poolConfig.setMinIdle(redisInfo.getPool().getMinIdle());
//        poolConfig.setTestOnBorrow(false);
//        poolConfig.setTestWhileIdle(true);
//        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(redisInfo.getPool().getTimeBetweenEvictionRuns()));
//        JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder()
//                .readTimeout(Duration.ofMillis(redisInfo.getReadTimeout()))
//                .connectTimeout(Duration.ofMillis(redisInfo.getConnectTimeout()))
//                .usePooling().poolConfig(poolConfig)
//                .build();
//
//        return new JedisConnectionFactory(config, clientConfiguration);
//    }

    static RedisConnectionFactory createLettuceConnectFactory(ClusterConfigurationProperties redisInfo) {
        Assert.isTrue(redisInfo != null && redisInfo.getCluster() != null && redisInfo.getCluster().getNodes() != null && redisInfo.getCluster().getNodes().size() > 0, "请先配置redis");

        RedisClusterConfiguration config = new RedisClusterConfiguration(redisInfo.getCluster().getNodes());
        if (StringUtils.hasLength(redisInfo.password)) {
            config.setPassword(redisInfo.password);
        }
        config.setMaxRedirects(redisInfo.getMaxRedirects());

        GenericObjectPoolConfig poolConfig  = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(redisInfo.getPool().getMaxActive());
        poolConfig.setMaxIdle(redisInfo.getPool().getMaxIdle());
        poolConfig.setMinIdle(redisInfo.getPool().getMinIdle());
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(redisInfo.getPool().getTimeBetweenEvictionRuns()));

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .commandTimeout(Duration.ofMillis(redisInfo.getReadTimeout()));
        return new LettuceConnectionFactory(config,builder.build());
    }

}
