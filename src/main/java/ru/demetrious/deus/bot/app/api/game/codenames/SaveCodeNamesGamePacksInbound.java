package ru.demetrious.deus.bot.app.api.game.codenames;

import org.springframework.web.multipart.MultipartFile;

@FunctionalInterface
public interface SaveCodeNamesGamePacksInbound {
    void savePacks(MultipartFile[] packs);
}
