package ru.demetrious.deus.bot.app.player.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddedInfo {
    private String title;
    private int count = 1;
    private long duration;
    private String url;
    private boolean isLive;
    private String preview = "https://i.imgur.com/7SdVZxF.png";
}
