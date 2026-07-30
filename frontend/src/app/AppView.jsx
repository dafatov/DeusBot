import {NotFound} from '@pages/not-found/NotFound';
import {GameType} from '@shared/lib/game/GameType';
import {SocketProvider} from '@shared/lib/socket/SocketProvider';
import {Route, Routes, useParams} from 'react-router-dom';

const GamePage = () => {
  const {gameType, gameId} = useParams();
  const Game = GameType[gameType];
  if (!Game) return <NotFound/>;
  const Provider = Game.provider;
  const Component = Game.component;

  return (
    <SocketProvider>
      <Provider gameId={gameId}>
        <Component/>
      </Provider>
    </SocketProvider>
  );
};

const GameLobbyPage = () => {
  const {gameType} = useParams();
  const Game = GameType[gameType];
  if (!Game) return <NotFound/>;
  return Game.lobby();
};

export const AppView = () => (
  <Routes>
    <Route path="/login/success" element={<>Успех</>}/>
    <Route path="/login/failure" element={<>Провал</>}/>
    <Route path="/game/:gameType" element={<GameLobbyPage/>}/>
    <Route path="/game/:gameType/:gameId" element={<GamePage/>}/>
    <Route path="*" element={<NotFound/>}/>
  </Routes>
);
