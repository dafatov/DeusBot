export const startGame = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'cross_ward.start_game'}));
};

export const shufflePlayers = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'cross_ward.shuffle_players'}));
};

export const toggleLocked = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'cross_ward.set_locked'}));
};

export const togglePause = (send, gameId) => {
  send(`/app/game/${gameId}`, JSON.stringify({type: 'cross_ward.set_pause'}));
};
