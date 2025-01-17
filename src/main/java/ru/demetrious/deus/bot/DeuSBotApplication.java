package ru.demetrious.deus.bot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import static org.springframework.boot.SpringApplication.run;

@EnableAspectJAutoProxy
@EnableFeignClients
@SpringBootApplication
public class DeuSBotApplication {
    public static void main(String[] args) {
        run(DeuSBotApplication.class, args);
    }
}
