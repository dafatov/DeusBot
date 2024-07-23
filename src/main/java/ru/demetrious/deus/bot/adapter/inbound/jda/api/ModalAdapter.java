package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import java.util.List;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

public interface ModalAdapter extends GenericInteractionAdapter<ModalInteraction> {
    List<String> getValues();
}
