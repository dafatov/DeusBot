package ru.demetrious.deus.bot.fw.config.cache;

import java.time.Duration;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("cache")
public class CacheProperties {
    private Map<String, CacheProperty> configs;

    @Data
    public static class CacheProperty {
        private Duration expireAfterWrite;
    }
}
