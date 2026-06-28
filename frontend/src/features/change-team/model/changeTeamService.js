export const changeTeam = (send, gameId, team, captain) =>
  send(`/app/game/${gameId}`, JSON.stringify({type: 'change_team', team, captain}));
