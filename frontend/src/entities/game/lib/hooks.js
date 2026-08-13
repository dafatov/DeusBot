import {useContext, useEffect, useState} from 'react';
import {GameContext} from './GameContext';

export const useGame = () => {
  const context = useContext(GameContext);
  if (!context) {
    throw new Error('useGame must be used within GameProvider');
  }

  return context;
};

export const useGameSessionStorage = (key, initial) => {
  const {gameType, gameId} = useGame();
  const storageKey = `${gameType}_${gameId}_${key}`;
  const [data, setData] = useState(() => {
    try {
      const stored = sessionStorage.getItem(storageKey);

      if (stored) {
        return JSON.parse(stored);
      }
    } catch (e) {
      console.warn('Wrong data in session storage. Dropped');
    }
    return typeof initial === 'function' ? initial() : initial;
  });

  useEffect(() => {
    try {
      if (data) {
        sessionStorage.setItem(storageKey, JSON.stringify(data));
      } else {
        sessionStorage.removeItem(storageKey);
      }
    } catch (e) {
      console.error(e);
    }
  }, [storageKey, data]);

  return [data, setData];
};
