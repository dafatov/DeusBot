package ru.demetrious.deus.bot.fw.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.benmanes.caffeine.cache.Caffeine.newBuilder;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {
    private final CacheProperties cacheProperties;

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        return buildCacheManager();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private CaffeineCacheManager buildCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();

        cacheProperties.getConfigs().entrySet().forEach(entry -> registerEntry(caffeineCacheManager, entry));
        return caffeineCacheManager;
    }

    private void registerEntry(CaffeineCacheManager caffeineCacheManager, Map.Entry<String, CacheProperties.CacheProperty> entry) {
        Caffeine<Object, Object> builder = newBuilder();

        builder.expireAfterWrite(entry.getValue().getExpireAfterWrite());

        caffeineCacheManager.registerCustomCache(entry.getKey(), builder.build());
    }
}
