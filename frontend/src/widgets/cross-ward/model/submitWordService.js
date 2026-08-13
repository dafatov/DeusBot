export const submitWord = (send, gameId, wordId, word) =>
  send(`/app/game/${gameId}`, JSON.stringify({type: 'cross_ward.submit_word', wordId, word}));

export const skipTurn = (send, gameId) =>
  send(`/app/game/${gameId}`, JSON.stringify({type: 'cross_ward.skip_turn'}));
