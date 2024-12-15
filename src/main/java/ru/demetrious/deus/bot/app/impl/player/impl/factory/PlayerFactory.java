package ru.demetrious.deus.bot.app.impl.player.impl.factory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.player.ConnectOutbound;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.app.impl.player.impl.PlayerImpl;
import ru.demetrious.deus.bot.app.impl.player.impl.SchedulerImpl;
import ru.demetrious.deus.bot.app.impl.player.impl.handler.AudioEventAdapterImpl;

import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlayerFactory {
    private final AudioPlayerManager audioPlayerManager;
    private final List<ConnectOutbound<?>> connectOutbound;

    public Player create(String guildId) {
        AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
        SchedulerImpl scheduler = new SchedulerImpl(audioPlayer);

        audioPlayer.addListener(new AudioEventAdapterImpl(scheduler));

        return new PlayerImpl(
            audioPlayerManager,
            audioPlayer,
            scheduler,
            b(connectOutbound)
        );
    }
}
