package ru.demetrious.deus.bot.app.api.modal;

import ru.demetrious.deus.bot.domain.ModalData;

@FunctionalInterface
public interface ShowModalOutbound {
    void showModal(ModalData modal);
}
