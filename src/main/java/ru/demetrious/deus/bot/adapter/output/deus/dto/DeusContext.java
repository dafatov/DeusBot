package ru.demetrious.deus.bot.adapter.output.deus.dto;

public record DeusContext(byte[] audio, String userId, String callback, String channelId) {
}
