package ru.demetrious.deus.bot.fw.aspect;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.audit.IncrementAuditOutbound;
import ru.demetrious.deus.bot.app.api.command.GetCommandNameOutbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.session.GetSessionOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.domain.Audit;
import ru.demetrious.deus.bot.domain.Session;

import static java.time.Duration.between;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static ru.demetrious.deus.bot.domain.Audit.Type.BUTTON;
import static ru.demetrious.deus.bot.domain.Audit.Type.COMMAND;
import static ru.demetrious.deus.bot.domain.Audit.Type.MESSAGE;
import static ru.demetrious.deus.bot.domain.Audit.Type.MODAL;
import static ru.demetrious.deus.bot.domain.Audit.Type.VOICE;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@RequiredArgsConstructor
@Aspect
@Component
public class AuditAspect {
    private final IncrementAuditOutbound incrementAuditOutbound;
    private final List<GetGuildIdOutbound<?>> getGuildIdOutbound;
    private final List<GetCommandNameOutbound<?>> getCommandNameOutbound;
    private final List<GetUserIdOutbound<?>> getUserIdOutbound;
    private final GetSessionOutbound getSessionOutbound;

    @After("within(ru.demetrious.deus.bot.app.api.command.CommandInbound+) && execution(* execute(..))")
    public void commandExecuteAudit() {
        incrementAuditOutbound.incrementAudit(createCommandAuditId().setType(COMMAND));
    }

    @After("within(ru.demetrious.deus.bot.app.api.command.CommandInbound+) && execution(* onButton(..))")
    public void commandOnButtonAudit() {
        incrementAuditOutbound.incrementAudit(createCommandAuditId().setType(BUTTON));
    }

    @After("within(ru.demetrious.deus.bot.app.api.command.CommandInbound+) && execution(* onModal(..))")
    public void commandOnModalAudit() {
        incrementAuditOutbound.incrementAudit(createCommandAuditId().setType(MODAL));
    }

    @After(value = "within(ru.demetrious.deus.bot.app.api.message.MessageReceivedInbound+) && execution(* execute(..)) && args(guildId, userId)", argNames = "guildId,userId")
    public void messageReceivedAudit(String guildId, String userId) {
        incrementAuditOutbound.incrementAudit(new Audit.AuditId()
            .setGuildId(guildId)
            .setUserId(userId)
            .setType(MESSAGE)
            .setName(EMPTY));
    }

    @After(value = "within(ru.demetrious.deus.bot.app.api.voice.GuildVoiceSessionUpdateInbound+) && execution(* execute(..)) && args(guildId, userId, isJoined)", argNames = "guildId,userId,isJoined")
    public void durationVoiceSessionAudit(String guildId, String userId, boolean isJoined) {
        if (isJoined) {
            return;
        }

        getSessionOutbound.getSession(new Session.SessionId()
                .setGuildId(guildId)
                .setUserId(userId))
            .map(session -> between(session.getStart(), session.getFinish()))
            .map(Duration::getSeconds)
            .ifPresent(duration -> incrementAuditOutbound.incrementAudit(
                new Audit.AuditId()
                    .setGuildId(guildId)
                    .setUserId(userId)
                    .setType(VOICE)
                    .setName(EMPTY),
                duration
            ));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Audit.AuditId createCommandAuditId() {
        return new Audit.AuditId()
            .setGuildId(b(getGuildIdOutbound).getGuildId())
            .setUserId(b(getUserIdOutbound).getUserId())
            .setName(b(getCommandNameOutbound).getCommandName().name());
    }
}
