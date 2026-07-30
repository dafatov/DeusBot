export const addHint = (send, gameId, word, count) =>
  send(`/app/game/${gameId}`, JSON.stringify({type: 'code_names.add_hint', word, count}));

export const setHintGuessed = (send, gameId, team, word, guessed) =>
  send(`/app/game/${gameId}`, JSON.stringify({type: 'code_names.set_hint_guessed', team, word, guessed}));
