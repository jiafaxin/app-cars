package com.autohome.app.cars.common.redis;

import lombok.Data;
import java.util.List;

@Data
public class ClusterConfigurationProperties {

    String password;

    int readTimeout = 5000;

    int connectTimeout = 5000;

    int maxRedirects = 5;

    Pool pool;

    Cluster cluster;

    public Pool getPool() {
        if (pool == null) {
            pool = new Pool();
            pool.setMaxActive(100);
            pool.setMaxIdle(100);
            pool.setMinIdle(10);
            pool.setTimeBetweenEvictionRuns(60000);
        }
        return pool;
    }

    @Data
    public static class Pool {
        int maxActive;
        int maxIdle;
        int minIdle;
        int maxWait;
        int timeBetweenEvictionRuns;
    }

    @Data
    public static class Cluster {
        List<String> nodes;
    }

}
