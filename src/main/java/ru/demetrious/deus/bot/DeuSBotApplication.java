package ru.demetrious.deus.bot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableAsync
@EnableCaching
@EnableAspectJAutoProxy
@EnableFeignClients
@EnableWebSocketMessageBroker
@ConfigurationPropertiesScan
@SpringBootApplication
public class DeuSBotApplication {
    static void main(String[] args) {
        new SpringApplicationBuilder(DeuSBotApplication.class)
            .run(args);
    }
}
