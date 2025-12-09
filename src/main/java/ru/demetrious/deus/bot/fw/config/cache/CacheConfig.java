package ru.demetrious.deus.bot.fw.config.cache;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.cache.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static javax.cache.Caching.getCachingProvider;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.ExpiryPolicyBuilder.timeToLiveExpiration;
import static org.ehcache.config.builders.ResourcePoolsBuilder.newResourcePoolsBuilder;
import static org.ehcache.config.units.EntryUnit.ENTRIES;
import static org.ehcache.config.units.MemoryUnit.MB;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CacheConfig {
    private final CacheProperties cacheProperties;

    @Bean
    public JCacheCacheManager jCacheCacheManager() {
        var springCacheManager = new JCacheCacheManager();

        springCacheManager.setCacheManager(ehcacheManager());
        return springCacheManager;
    }

    @Bean(destroyMethod = "close")
    public CacheManager ehcacheManager() {
        return buildCacheManager();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private CacheManager buildCacheManager() {
        Map<String, CacheConfiguration<?, ?>> caches = new HashMap<>();

        cacheProperties.getConfigs().forEach((cacheName, cacheProperty) -> {
            var config = newCacheConfigurationBuilder(
                Serializable.class,
                Serializable.class,
                newResourcePoolsBuilder()
                    .heap(500, ENTRIES)
                    .disk(500, MB, true))
                .withExpiry(timeToLiveExpiration(cacheProperty.getExpireAfterWrite()))
                .build();

            caches.put(cacheName, config);
        });


        return createJCacheCacheManager(caches);
    }

    private CacheManager createJCacheCacheManager(Map<String, CacheConfiguration<?, ?>> caches) {
        var ehcacheProvider = (EhcacheCachingProvider) getCachingProvider();
        var persistenceConfig = new DefaultPersistenceConfiguration(new File(cacheProperties.getFilesPath()));
        var configuration = new DefaultConfiguration(
            caches,
            ehcacheProvider.getDefaultClassLoader(),
            persistenceConfig
        );

        return ehcacheProvider.getCacheManager(ehcacheProvider.getDefaultURI(), configuration);
    }
}
