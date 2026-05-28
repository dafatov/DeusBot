export const startGame = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'start_game'}));
};

export const shufflePlayers = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'shuffle_players'}));
};

export const toggleLocked = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'set_locked'}));
};

export const togglePause = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'set_pause'}));
};
