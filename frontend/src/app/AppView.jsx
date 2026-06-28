import {CodeNamesLobby} from '@pages/code-names-lobby/CodeNamesLobby';
import {CodeNames} from '@pages/code-names/CodeNames';
import {NotFound} from '@pages/not-found/NotFound';
import {Route, Routes} from 'react-router-dom';

export const AppView = () => (
  <Routes>
    <Route path="/login/success" element={<>Успех</>}/>
    <Route path="/login/failure" element={<>Провал</>}/>
    <Route path="/game/code-names" element={<CodeNamesLobby/>}/>
    <Route path="/game/code-names/:gameId" element={<CodeNames/>}/>
    <Route path="*" element={<NotFound/>}/>
  </Routes>
);
