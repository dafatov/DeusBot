import {useGame} from '@entities/game/lib/hooks';
import {CrossWardControl} from '@features/game-control';
import {setSpectator} from '@features/set-spectator/model/setSpectatorService';
import {ArrowRightAlt, LoginRounded} from '@mui/icons-material';
import {alpha, IconButton, List, ListItem, ListItemAvatar, ListItemIcon, ListItemText, Paper, Stack} from '@mui/material';
import {PanZoomProvider} from '@shared/lib/pan-zoom/PanZoomProvider';
import {useSocket} from '@shared/lib/socket/hooks';
import {DiscordAvatar} from '@shared/ui/DiscordAvatar';
import {useEffect} from 'react';
import {useTimer} from 'react-timer-hook';
import {CrosswordCanvas} from '../cross-ward-canvas';
import {CrossWardSpectatorPlayers} from '../spectators';

export const CrossWardContent = () => {
  const {send} = useSocket();
  const {gameId, me: {isHost, isSpectator}, phase, players, locked, currentPlayer, timer, paused} = useGame();
  const {minutes, seconds, restart, isRunning} = useTimer({expiryTimestamp: new Date(), autoStart: false});

  const handleClick = () => setSpectator(send, gameId, false);

  useEffect(() => {
    if (phase === 'PLAYING') {
      restart(new Date(Date.now() + (timer * 1000)), !paused);
    } else {
      restart(new Date(Date.now()), false);
    }
  }, [phase, timer, paused, restart]);

  return (
    <Stack container direction="column" sx={{height: '100vh'}}>
      <CrossWardSpectatorPlayers/>
      <Paper sx={{
        position: 'absolute',
        top: '50%',
        transform: 'translateY(-50%)',
        right: 16,
        zIndex: 2,
        minWidth: '100px',
        opacity: 0.8,
      }}>
        <List>
          <ListItem>
            <ListItemText>
              {(isRunning || paused) && `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`}
            </ListItemText>
          </ListItem>
          {players.map((player, index) => (
            <ListItem key={player.id} sx={{backgroundColor: alpha(player.color, 0.5)}}>
              <ListItemIcon>{index === currentPlayer && phase === 'PLAYING' && <ArrowRightAlt/>}</ListItemIcon>
              <ListItemAvatar>
                <DiscordAvatar
                  id={player.id}
                  name={player.name}
                  avatar={player.avatar}
                  disconnected={player.disconnected}
                />
              </ListItemAvatar>
              <ListItemText>{player.score}</ListItemText>
            </ListItem>
          ))}
          {isSpectator && !locked && <ListItem>
            <IconButton onClick={handleClick}><LoginRounded/></IconButton>
          </ListItem>}
        </List>
      </Paper>
      <PanZoomProvider>
        <CrosswordCanvas/>
      </PanZoomProvider>
      {isHost
        ? <CrossWardControl/>
        : <></>}
    </Stack>
  );
};
