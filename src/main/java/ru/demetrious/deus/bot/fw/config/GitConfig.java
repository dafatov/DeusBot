package ru.demetrious.deus.bot.fw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// The property will be generated at the maven "initialization" stage
@PropertySource("classpath:git.properties")
@Configuration
public class GitConfig {
}
