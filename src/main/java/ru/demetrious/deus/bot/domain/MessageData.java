package ru.demetrious.deus.bot.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageData {
    private String content;
    private List<MessageEmbed> embeds = new ArrayList<>();
}
