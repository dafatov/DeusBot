package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.domain.ButtonComponent.EmojiEnum;

import static java.lang.Thread.currentThread;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Component
public class EmojiProvider {
    private final JDA jda;

    @NotNull
    public ApplicationEmoji getApplicationEmoji(EmojiEnum emoji) {
        return jda.retrieveApplicationEmojis()
            .complete().stream()
            .filter(applicationEmoji -> applicationEmoji.getName().equals(emoji.getName()))
            .findFirst()
            .orElseGet(() -> createApplicationEmoji(emoji));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private ApplicationEmoji createApplicationEmoji(EmojiEnum emoji) {
        try (InputStream iconStream = currentThread().getContextClassLoader().getResourceAsStream(emoji.getIcon().toString())) {
            return jda.createApplicationEmoji(emoji.getName(), Icon.from(requireNonNull(iconStream)))
                .complete();
        } catch (IOException e) {
            throw new IllegalStateException("Can't create application emoji with name=%s".formatted(emoji), e);
        }
    }
}
