package ru.demetrious.deus.bot.fw.config.ocr;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import static nu.pattern.OpenCV.loadLocally;

@Configuration
public class OpenCVConfig {
    @PostConstruct
    public void initOpenCV() {
        loadLocally();
    }
}
