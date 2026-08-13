import {useGame} from '@entities/game/lib/hooks';
import {CrossWardControl} from '@features/game-control';
import {setSpectator} from '@features/set-spectator/model/setSpectatorService';
import {ArrowRightAlt, EmojiEventsOutlined} from '@mui/icons-material';
import {alpha, Button, Divider, List, ListItem, ListItemAvatar, ListItemIcon, ListItemText, Paper, Stack, Typography} from '@mui/material';
import beepSound from '@shared/assets/sample/beep.wav';
import {PanZoomProvider} from '@shared/lib/pan-zoom/PanZoomProvider';
import {useSocket} from '@shared/lib/socket/hooks';
import {DiscordAvatar} from '@shared/ui/DiscordAvatar';
import {useEffect, useMemo, useRef} from 'react';
import {useTimer} from 'react-timer-hook';
import useSound from 'use-sound';
import {CrosswordCanvas} from '../cross-ward-canvas';
import {CrossWardSpectatorPlayers} from '../spectators';
import {skipTurn, submitWord} from './model/submitWordService';

export const CrossWardContent = () => {
  const {send} = useSocket();
  const {gameId, me: {isHost, isSpectator}, phase, players, locked, currentPlayer, timer, paused} = useGame();
  const {minutes, seconds, restart, isRunning} = useTimer({expiryTimestamp: new Date(), autoStart: false});
  // noinspection JSCheckFunctionSignatures
  const [play] = useSound(beepSound, {volume: 0.1});
  const prevCurrentPlayerRef = useRef(currentPlayer);

  useEffect(() => {
    if (minutes === 0 && seconds > 0 && seconds < 10) {
      play();
    }
  }, [minutes, seconds, play]);

  useEffect(() => {
    const prevCurrentPlayer = prevCurrentPlayerRef?.current;

    // TODO возможно добавить раунды (так как при одном игроке звука не будет при передаче хода)
    if (phase === 'PLAYING' && prevCurrentPlayer !== currentPlayer) {
      play();
    }

    prevCurrentPlayerRef.current = currentPlayer;
  }, [currentPlayer, phase, play]);

  const handleBecomePlayerClick = () => setSpectator(send, gameId, false);

  const handleSkipTurnClick = () => skipTurn(send, gameId);

  const handleWordSubmit = (wordId, word) => {
    return submitWord(send, gameId, wordId, word);
  };

  useEffect(() => {
    if (phase === 'PLAYING') {
      restart(new Date(Date.now() + (timer * 1000)), !paused);
    } else {
      restart(new Date(Date.now()), false);
    }
  }, [phase, timer, paused, restart]);

  const color = useMemo(() => {
    if (minutes === 0 && seconds < 10) {
      return 'error';
    } else if (minutes === 0 && seconds < 30) {
      return 'warning';
    } else {
      return 'primary';
    }
  }, [minutes, seconds]);

  return (
    <Stack container direction="column" sx={{height: '100vh'}}>
      <CrossWardSpectatorPlayers/>
      <Paper sx={{
        position: 'absolute',
        top: '50%',
        transform: 'translateY(-50%)',
        right: 16,
        zIndex: 2,
        minWidth: '200px',
        opacity: 0.8,
      }}>
        <Stack direction="row" sx={{justifyContent: 'space-evenly', alignItems: 'center'}}>
          <Typography color="primary" sx={t => ({
            textAlign: 'center',
            padding: t.spacing(), flex: 1
          })}>до 100</Typography>
          <Divider orientation="vertical" flexItem/>
          {(isRunning || paused) && <Typography color={color}
                                                sx={t => ({
                                                  textAlign: 'center',
                                                  padding: t.spacing(), flex: 1
                                                })}>{String(minutes).padStart(2, '0')}:{String(seconds).padStart(2, '0')}</Typography>}
        </Stack>
        <Divider/>
        <List>
          {players.map(player => (
            <ListItem key={player.id} sx={{backgroundColor: alpha(player.color, 0.5)}}>
              <ListItemIcon>{player.id === currentPlayer && (phase === 'PLAYING' && <ArrowRightAlt/> || phase === 'FINISHED' &&
                <EmojiEventsOutlined/>)}</ListItemIcon>
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
          {isSpectator && !locked &&
            <Button onClick={handleBecomePlayerClick} color="primary" sx={t => ({
              textAlign: 'center',
              width: '100%',
              minHeight: '40px',
              padding: t.spacing(2)
            })}>Присоединиться</Button>
          }
        </List>
        {phase === 'PLAYING' && <><Divider/>
          <Button onClick={handleSkipTurnClick} color="primary"
                  sx={t => ({textAlign: 'center', width: '100%', padding: t.spacing()})}>Пропустить</Button></>}
      </Paper>
      <PanZoomProvider>
        <CrosswordCanvas onWordSubmit={handleWordSubmit}/>
      </PanZoomProvider>
      {isHost
        ? <CrossWardControl/>
        : <></>}
    </Stack>
  );
};
