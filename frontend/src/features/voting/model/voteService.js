export const sendVote = (send, gameId, vote) =>
  send(`/app/game/${gameId}`, JSON.stringify({type: 'code_names.vote', vote}));
