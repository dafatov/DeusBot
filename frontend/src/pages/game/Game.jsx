import {GameTypes} from '@app/config/GameTypes';
import {NotFound} from '@pages/not-found/NotFound';
import {SocketProvider} from '@shared/lib/socket/SocketProvider';
import {useParams} from 'react-router-dom';

export const Game = () => {
  const {gameType, gameId} = useParams();
  const Game = GameTypes[gameType];
  const Provider = Game?.provider;
  const Component = Game?.component;

  if (!Provider || !Component) return <NotFound/>;

  return (
    <SocketProvider>
      <Provider gameId={gameId}>
        <Component/>
      </Provider>
    </SocketProvider>
  );
};
