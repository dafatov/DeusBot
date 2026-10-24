import {GameTypes} from '@app/config/GameTypes';
import {NotFound} from '@pages/not-found/NotFound';
import {useParams} from 'react-router-dom';


export const GameLobby = () => {
  const {gameType} = useParams();
  const Game = GameTypes[gameType];

  if (!Game) return <NotFound/>;

  return Game.lobby();
};
