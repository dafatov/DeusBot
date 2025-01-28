package ru.demetrious.deus.bot.app.impl.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.player.DisconnectOutbound;

@RequiredArgsConstructor
@Slf4j
@Component
public class LeaveGuildPlayerJob extends QuartzJobBean {
    private final DisconnectOutbound disconnectOutbound;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        String guildId = context.getTrigger().getKey().getName();

        disconnectOutbound.disconnect(guildId);
        log.info("Успешно вышел из голосового канала сервера {}", guildId);
    }
}
