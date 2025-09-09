package ru.demetrious.deus.bot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@EnableAspectJAutoProxy
@EnableFeignClients
@ConfigurationPropertiesScan
@SpringBootApplication
public class DeuSBotApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DeuSBotApplication.class)
            .run(args);
    }
}
