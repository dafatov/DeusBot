export const setSpectator = (send, gameId, spectator) =>
  send(`/app/game/${gameId}`, JSON.stringify({type: 'cross_ward.set_spectator', spectator}));
