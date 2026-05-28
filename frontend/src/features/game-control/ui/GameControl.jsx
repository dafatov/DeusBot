import {LockOpenOutlined, LockOutlined, PauseOutlined, PlayArrowOutlined, RestartAlt, Shuffle} from '@mui/icons-material';
import {Box, Fab} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {useSocket} from '@shared/lib/socket/hooks';
import {shufflePlayers, startGame, toggleLocked, togglePause} from '../model/gameControlService.js';

export const GameControl = () => {
  const {gameId, phase, locked, paused} = useGame();
  const {send} = useSocket();

  return (
    <Box sx={t => ({
      position: 'absolute',
      bottom: 16,
      right: 16,
      display: 'flex',
      flexDirection: 'row-reverse',
      gap: t.spacing(),
    })}>
      <Fab color="primary" disabled={locked} onClick={() => startGame(send, gameId)}>
        {phase === 'WAITING' || phase === 'FINISHED' ? <PlayArrowOutlined/> : <RestartAlt/>}
      </Fab>
      <Fab color="primary" disabled={locked} onClick={() => shufflePlayers(send, gameId)}>
        <Shuffle/>
      </Fab>
      <Fab color="primary" onClick={() => toggleLocked(send, gameId)}>
        {locked ? <LockOpenOutlined/> : <LockOutlined/>}
      </Fab>
      {(phase === 'HINTING' || phase === 'GUESSING') &&
        <Fab color="primary" disabled={locked} onClick={() => togglePause(send, gameId)}>
          {paused ? <PlayArrowOutlined/> : <PauseOutlined/>}
        </Fab>}
    </Box>
  );
};
