package ru.demetrious.deus.bot.app.api.game;

import org.springframework.web.multipart.MultipartFile;

@FunctionalInterface
public interface SaveGamePacksInbound {
    void savePacks(MultipartFile[] packs);
}
