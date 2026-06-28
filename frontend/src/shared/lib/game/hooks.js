import {GameContext} from '@shared/lib/game/GameContext';
import {useContext} from 'react';

export const useGame = () => {
  const context = useContext(GameContext);
  if (!context) {
    throw new Error('useGame must be used within GameProvider');
  }
  return context;
};
