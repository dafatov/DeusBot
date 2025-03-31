package ru.demetrious.deus.bot.app.api.modal;

import net.dv8tion.jda.api.interactions.modals.Modal;
import ru.demetrious.deus.bot.domain.ModalData;

@FunctionalInterface
public interface ShowModalOutbound {
    int MAX_COMPONENTS = Modal.MAX_COMPONENTS;

    void showModal(ModalData modal);
}
