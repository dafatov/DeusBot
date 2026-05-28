import {PlayerCard} from '@entities/user';
import {BecomeCaptainButton, BecomePlayerButton} from '@features/change-team';
import {GuessZone} from '@features/hinting/ui/GuessZone';
import {HintItem} from '@features/hinting/ui/HintItem';
import {VoteZone} from '@features/voting';
import {Card, Divider, List, Typography} from '@mui/material';
import beepSound from '@shared/assets/sample/beep.wav';
import {useGame} from '@shared/lib/game/hooks';
import {useEffect, useRef} from 'react';
import {useTimer} from 'react-timer-hook';
import useSound from 'use-sound';

export const TeamZone = ({currentTeam}) => {
  const {gameId, team, phase, locked, paused, timer, findCaptain, filterPlayers, filterHints, me, playersBySkip} = useGame();
  const {minutes, seconds, restart, isRunning} = useTimer({expiryTimestamp: new Date(), autoStart: false});
  // noinspection JSCheckFunctionSignatures
  const [play] = useSound(beepSound);
  const prevPhaseRef = useRef(phase);

  useEffect(() => {
    if (currentTeam === team && (phase === 'HINTING' || phase === 'GUESSING')) {
      restart(new Date(Date.now() + (timer * 1000)), !paused);
    } else {
      restart(new Date(Date.now()), false);
    }
  }, [phase, timer, team, paused, restart, currentTeam]);

  useEffect(() => {
    if (minutes === 0 && seconds > 0 && seconds < 10) {
      play();
    }
  }, [minutes, seconds, play]);

  useEffect(() => {
    const prevPhase = prevPhaseRef.current;

    if (prevPhase !== 'GUESSING' && phase === 'GUESSING' || prevPhase !== 'HINTING' && phase === 'HINTING') {
      play();
    }

    prevPhaseRef.current = phase;
  }, [phase, play]);

  const captain = findCaptain(currentTeam);
  const players = filterPlayers(currentTeam);
  const hints = filterHints(currentTeam);

  return (
    <Card sx={{minWidth: '350px', minHeight: '500px', display: 'flex', flexDirection: 'column', opacity: 0.9}}>
      {captain
        ? <PlayerCard player={captain}/>
        : locked
          ? <Typography sx={t => ({
            minHeight: '56.5px',
            justifyContent: 'center',
            display: 'flex',
            alignItems: 'center',
            color: t.palette.primary.main
          })}>Отсутствует</Typography>
          : <BecomeCaptainButton gameId={gameId} team={currentTeam}/>}
      <Divider/>
      <List sx={{flex: 1}}>
        {players.map(player => <PlayerCard player={player}/>)}
        {locked
          ? players.length === 0 && <Typography sx={t => ({
          minHeight: '56.5px',
          justifyContent: 'center',
          display: 'flex',
          alignItems: 'center',
          color: t.palette.primary.main
        })}>Отсутствуют</Typography>
          : currentTeam !== me.team || me.isCaptain
            ? <BecomePlayerButton gameId={gameId} team={currentTeam}/>
            : <></>}
      </List>
      {hints.length > 0 && <>
        <Divider textAlign="right">
          {currentTeam === team && (isRunning || paused) && `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`}
        </Divider>
        <List>
          {hints.map((h, i) => <HintItem key={i} hint={h} currentTeam={currentTeam}/>)}
        </List>
      </>}
      {currentTeam === team && (phase === 'HINTING' || phase === 'GUESSING') && <>
        <Divider textAlign="right">
          {hints.length <= 0 && (isRunning || paused) && `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`}
        </Divider>
        {me.isCaptain && me.team === team && phase === 'HINTING' &&
          <GuessZone currentTeam={currentTeam}/>}
        {!me.isCaptain && me.team === team && phase === 'GUESSING' &&
          <VoteZone playerList={playersBySkip} vote={{type: 'skip'}} clickable={!paused}>
            <Typography color="primary" sx={{textAlign: 'center'}}>Пропустить</Typography>
          </VoteZone>}
      </>}
    </Card>
  );
};
