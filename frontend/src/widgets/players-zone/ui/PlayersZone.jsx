import {useGame} from '@entities/game/lib/hooks';
import {setSpectator} from '@features/set-spectator/model/setSpectatorService';
import {Bolt, BoltOutlined, EmojiEventsOutlined} from '@mui/icons-material';
import {alpha, Button, Divider, List, ListItem, ListItemAvatar, ListItemIcon, ListItemText, Paper, Rating, Stack, Typography} from '@mui/material';
import beepSound from '@shared/assets/sample/beep.wav';
import {useSocket} from '@shared/lib/socket/hooks';
import {DiscordAvatar} from '@shared/ui/DiscordAvatar';
import {useEffect, useMemo, useRef} from 'react';
import {useTimer} from 'react-timer-hook';
import useSound from 'use-sound';
import {skipTurn} from '../../cross-ward/model/submitWordService';

export const PlayersZone = ({}) => {
  const {send} = useSocket();
  const {gameId, me: {isSpectator, isCurrentPlayer}, phase, players, locked, currentPlayer, currentEnergy, timer, paused} = useGame();
  const {minutes, seconds, restart, isRunning} = useTimer({expiryTimestamp: new Date(), autoStart: false});
  // noinspection JSCheckFunctionSignatures
  const [play] = useSound(beepSound, {volume: 0.1});
  const prevCurrentPlayerRef = useRef(currentPlayer);

  const handleBecomePlayerClick = () => setSpectator(send, gameId, false);

  const handleSkipTurnClick = () => skipTurn(send, gameId);

  useEffect(() => {
    if (minutes === 0 && seconds > 0 && seconds < 10) {
      play();
    }
  }, [minutes, seconds, play]);

  useEffect(() => {
    const prevCurrentPlayer = prevCurrentPlayerRef?.current;

    if (phase === 'PLAYING' && prevCurrentPlayer !== currentPlayer) {
      play();
    }

    prevCurrentPlayerRef.current = currentPlayer;
  }, [currentPlayer, phase, play]);

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
            <ListItemIcon>{player.id === currentPlayer && (phase === 'PLAYING' &&
              <Rating
                readOnly
                icon={<Bolt sx={{color: '#50ef50', width: '24px', height: '24px'}}/>}
                emptyIcon={<BoltOutlined sx={{width: '24px', height: '24px'}}/>}
                value={currentEnergy}
                max={2}
                defaultValue={currentEnergy}
                size="small"
              />
              || phase === 'FINISHED' &&
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
        {isSpectator && !locked
          ? <Button onClick={handleBecomePlayerClick} color="primary" sx={t => ({
            textAlign: 'center',
            width: '100%',
            minHeight: '40px',
            padding: t.spacing(2)
          })}>Присоединиться</Button>
          : players?.length <= 0 && <Typography sx={t => ({
          minHeight: '56.5px',
          justifyContent: 'center',
          display: 'flex',
          alignItems: 'center',
          color: t.palette.primary.main
        })}>Отсутствуют</Typography>
        }
      </List>
      {phase === 'PLAYING' && isCurrentPlayer && <>
        <Divider/>
        <Button disabled={paused} onClick={handleSkipTurnClick} color="primary"
                sx={t => ({textAlign: 'center', width: '100%', padding: t.spacing()})}>Пропустить</Button>
      </>}
    </Paper>
  );
};
