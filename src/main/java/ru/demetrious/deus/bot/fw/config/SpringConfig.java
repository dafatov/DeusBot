package ru.demetrious.deus.bot.fw.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.cloud.context.scope.thread.ThreadScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    public static final String SCOPE_THREAD = "thread";

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> beanFactory.registerScope(SCOPE_THREAD, new ThreadScope());
    }
}
