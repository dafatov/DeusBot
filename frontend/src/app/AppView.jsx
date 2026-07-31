import {GameLobby} from '@pages/game-lobby/GameLobby';
import {Game} from '@pages/game/Game';
import {NotFound} from '@pages/not-found/NotFound';
import {Route, Routes} from 'react-router-dom';

export const AppView = () => (
  <Routes>
    <Route path="/login/success" element={<>Успех</>}/>
    <Route path="/login/failure" element={<>Провал</>}/>
      <Route path="/game/:gameType" element={<GameLobby/>}/>
      <Route path="/game/:gameType/:gameId" element={<Game/>}/>
    <Route path="*" element={<NotFound/>}/>
  </Routes>
);
