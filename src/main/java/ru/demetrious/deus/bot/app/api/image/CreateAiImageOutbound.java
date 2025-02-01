package ru.demetrious.deus.bot.app.api.image;

import java.util.Optional;

@FunctionalInterface
public interface CreateAiImageOutbound {
    Optional<String> createAiImage(String prompt);
}
