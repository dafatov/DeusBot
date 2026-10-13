export const startGame = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'code_names.start_game'}));
};

export const shufflePlayers = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'code_names.shuffle_players'}));
};

export const toggleLocked = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'code_names.set_locked'}));
};

export const togglePause = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'code_names.set_pause'}));
};
