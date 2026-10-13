package ru.demetrious.deus.bot.adapter.duplex.ui.input;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.PackDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.SettingDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.PackMapper;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.SettingMapper;
import ru.demetrious.deus.bot.app.api.game.CreateGameInbound;
import ru.demetrious.deus.bot.app.api.game.DeleteGamePackInbound;
import ru.demetrious.deus.bot.app.api.game.GetGamePacksInbound;
import ru.demetrious.deus.bot.app.api.game.JoinGameInbound;
import ru.demetrious.deus.bot.app.api.game.SaveGamePacksInbound;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/game")
public class GameAdapter {
    private final CreateGameInbound createGameInbound;
    private final JoinGameInbound joinGameInbound;
    private final SaveGamePacksInbound saveGamePacksInbound;
    private final GetGamePacksInbound getGamePacksInbound;
    private final DeleteGamePackInbound deleteGamePackInbound;
    private final PackMapper packMapper;
    private final SettingMapper settingMapper;

    @PostMapping("/create")
    public String create(@RequestBody SettingDto settingDto) {
        return createGameInbound.execute(settingMapper.map(settingDto));
    }

    @PostMapping("/{gameId}/join")
    public void join(@PathVariable String gameId) {
        joinGameInbound.execute(gameId);
    }

    @PostMapping(value = "/packs", consumes = MULTIPART_FORM_DATA_VALUE)
    public List<PackDto> uploadPacks(@RequestParam("files") MultipartFile[] files) {
        log.debug("uploadPacks");
        saveGamePacksInbound.savePacks(files);
        return packMapper.map(getGamePacksInbound.execute());
    }

    @GetMapping("/packs")
    public List<PackDto> getPacks() {
        return packMapper.map(getGamePacksInbound.execute());
    }

    @DeleteMapping("/packs")
    public List<PackDto> deletePack(@RequestParam Long id) {
        deleteGamePackInbound.execute(id);
        return packMapper.map(getGamePacksInbound.execute());
    }
}
