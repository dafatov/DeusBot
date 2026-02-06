package ru.demetrious.deus.bot.app.api.modal;

import net.dv8tion.jda.api.interactions.modals.Modal;
import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;
import ru.demetrious.deus.bot.domain.ModalData;

public interface ShowModalOutbound<I extends Interaction> extends HasEventOutbound {
    int MAX_COMPONENTS = Modal.MAX_COMPONENTS;

    void showModal(ModalData modal);
}
