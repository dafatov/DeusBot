export const changeTeam = (send, gameId, team, captain) =>
  send(`/app/game/${gameId}`, JSON.stringify({type: 'code_names.change_team', team, captain}));
