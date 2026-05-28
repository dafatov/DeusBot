import {getUserId} from '@shared/lib/cookies';
import {GameContext} from '@shared/lib/game/GameContext';
import {useSocket, useSocketSubscription} from '@shared/lib/socket/hooks';
import {useCallback, useEffect, useState} from 'react';
import {useSnackbar} from '../snackbar/hooks';

export const GameProvider = ({children, gameId}) => {
  const {connected, send} = useSocket();
  const [game, setGame] = useState(null);
  const {showError} = useSnackbar();

  const handleGameUpdate = useCallback(message => {
    setGame(JSON.parse(message.body));
  }, [setGame]);

  const handleError = useCallback(message => {
    showError(JSON.parse(message.body).message);
  }, []);

  useSocketSubscription(`/user/game/${gameId}`, handleGameUpdate);

  useSocketSubscription('/user/error', handleError);

  useEffect(() => {
    if (connected && gameId) {
      send(`/app/game/${gameId}`, JSON.stringify({type: 'get_state'}));
    }
  }, [connected, gameId, send]);

  const me = (game?.playerList ?? []).find(p => getUserId() === p?.id);
  const value = {
    gameId: game?.key,
    score: game?.state?.score,
    team: game?.state?.team,
    phase: game?.state?.phase,
    locked: game?.state?.locked,
    paused: !!game?.state?.remaining,
    timer: game?.state?.timer ?? game?.state?.remaining,
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

  return (
    <GameContext.Provider value={value}>
      {children}
    </GameContext.Provider>
  );
};
