package com.arbitrage.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "arbitrage.cache-manager")
public class CacheManagerProperties {
    private List<CacheConfig> configs;

    @Data
    public static class CacheConfig {
        /**
         * cache key
         */
        private String key;
        /**
         * 过期时间，sec
         */
        private long second = 60;
    }
}

