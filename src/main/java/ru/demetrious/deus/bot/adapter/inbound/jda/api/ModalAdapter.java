package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import java.util.List;

public interface ModalAdapter extends GenericInteractionAdapter {
    List<String> getValues();
}
