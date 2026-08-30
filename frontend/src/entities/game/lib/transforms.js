import {getUserId} from '@shared/lib/cookies';
import {normalizeGrid} from '../utils/crossward/normalizeGrid';

export const transformCodeNames = game => {
  const me = (game?.playerList ?? []).find(p => getUserId() === p?.id);

  return {
    gameId: game?.key,
    score: game?.state?.score,
    team: game?.state?.team,
    phase: game?.state?.phase,
    locked: game?.state?.locked,
    paused: !!game?.timer?.remaining,
    timer: game?.timer?.timer ?? game?.timer?.remaining,
    spectators: (game?.playerList ?? []).filter(p => p.team === 'SPECTATOR'),
    playersBySkip: (game?.playerList ?? []).filter(p => game?.voteMap?.[p.id]?.type === 'skip'),
    words: game?.wordList ?? [],
    me: {
      isSpectator: me?.team === 'SPECTATOR',
      team: me?.team,
      isCaptain: me?.captain,
      isHost: game?.hostId === getUserId(),
    },
    findCaptain: team => (game?.playerList ?? []).find(p => p.team === team && p.captain),
    filterPlayers: team => (game?.playerList ?? []).filter(p => p.team === team && !p.captain),
    filterHints: team => (game?.hintList ?? []).filter(h => h.team === team),
    filterPlayersByWord: word => (game?.playerList ?? []).filter(p => game?.voteMap?.[p.id]?.word === word?.text),
  };
};

export const transformCrossWard = game => {
  const playersMap = new Map((game?.playerList ?? []).map(p => [p.id, p]));
  const me = playersMap.get(getUserId());
  const grid = normalizeGrid(game?.grid);

  return {
    gameId: game?.key,
    startedAt: game?.state?.startedAt,
    phase: game?.state?.phase,
    locked: game?.state?.locked,
    paused: !!game?.timer?.remaining,
    timer: game?.timer?.timer ?? game?.timer?.remaining,
    currentPlayer: game?.state?.currentPlayer,
    currentEnergy: game?.state?.currentEnergy,
    grid,
    spectators: (game?.playerList ?? []).filter(p => p.spectator),
    me: {
      isCurrentPlayer: me?.id === game?.state?.currentPlayer,
      isSpectator: me?.spectator,
      isHost: game?.hostId === getUserId(),
      color: me?.color,
    },
    players: (game?.activePlayers ?? []).map(p => playersMap.get(p)),
    words: game?.words ?? {},
    letterTags: game?.letterTags ?? {},
    history: new Map((game?.history ?? []).map(h => [h.id, h])),
    findPlayer: id => playersMap.get(id),
  };
};
