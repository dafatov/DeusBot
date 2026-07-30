import {GameContext} from '@shared/lib/game/GameContext';
import {useSocket, useSocketSubscription} from '@shared/lib/socket/hooks';
import {useCallback, useEffect, useState} from 'react';
import {useSnackbar} from '../snackbar/hooks';

export const GameProvider = ({children, gameId, gameType, transform}) => {
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
      send(`/app/game/${gameId}`, JSON.stringify({type: `${gameType}.get_state`}));
    }
  }, [connected, gameId, send]);

  const value = transform?.(game);

  return (
    <GameContext.Provider value={value}>
      {children}
    </GameContext.Provider>
  );
};
