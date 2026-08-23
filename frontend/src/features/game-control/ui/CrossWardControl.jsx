import {useGame} from '@entities/game/lib/hooks';
import {LockOpenOutlined, LockOutlined, PauseOutlined, PlayArrowOutlined, RestartAlt, Shuffle} from '@mui/icons-material';
import {Fab, Paper} from '@mui/material';
import {useSocket} from '@shared/lib/socket/hooks';
import {shufflePlayers, startGame, toggleLocked, togglePause} from '../model/crossWardControlService';

export const CrossWardControl = () => {
  const {gameId, phase, locked, paused, players} = useGame();
  const {send} = useSocket();

  return (
    <Paper sx={t => ({
      position: 'absolute',
      bottom: 16,
      right: 16,
      display: 'flex',
      flexDirection: 'row-reverse',
      gap: t.spacing(),
      padding: t.spacing(),
      opacity: 0.8,
    })}>
      <Fab color="primary" disabled={locked || players?.length <= 0} onClick={() => startGame(send, gameId)}>
        {phase === 'WAITING' || phase === 'FINISHED' ? <PlayArrowOutlined/> : <RestartAlt/>}
      </Fab>
      <Fab color="primary" disabled={locked || players?.length <= 0} onClick={() => shufflePlayers(send, gameId)}>
        <Shuffle/>
      </Fab>
      <Fab color="primary" onClick={() => toggleLocked(send, gameId)}>
        {locked ? <LockOpenOutlined/> : <LockOutlined/>}
      </Fab>
      {(phase === 'PLAYING') &&
        <Fab color="primary" disabled={locked} onClick={() => togglePause(send, gameId)}>
          {paused ? <PlayArrowOutlined/> : <PauseOutlined/>}
        </Fab>}
    </Paper>
  );
};
