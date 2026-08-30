import {useGame} from '@entities/game/lib/hooks';
import {CrossWardControl} from '@features/game-control';
import {Stack} from '@mui/material';
import {useAutoResetState} from '@shared/lib/auto-reset-state/hooks';
import {PanZoomProvider} from '@shared/lib/pan-zoom/PanZoomProvider';
import {useSocket} from '@shared/lib/socket/hooks';
import {useState} from 'react';
import {CrosswordCanvas} from '../cross-ward-canvas';
import {HistoryZone} from '../history-zone/ui/HistoryZone';
import {PlayersZone} from '../players-zone/ui/PlayersZone';
import {CrossWardSpectatorPlayers} from '../spectators';
import {SpellZone} from '../spell-zone';
import {submitWord, useSpell} from './model/submitWordService';

export const CrossWardContent = () => {
  const {send} = useSocket();
  const {gameId, phase, me: {isHost}, grid: {shift}} = useGame();

  const [activeSpell, setActiveSpell] = useState(null);
  const [selectedWord, setSelectedWord] = useState();
  const [historyHighlight, setHistoryHighlight] = useAutoResetState(2000);

  const handleWordSubmit = (wordId, word) => {
    return submitWord(send, gameId, wordId, word);
  };

  const handleSpellClick = data => {
    const request = activeSpell.onCellClick(data);
    const {x, y, group, ...others} = request;

    if (others.type && (group === 'area' && x != null && y != null || group === 'word' && others.wordId != null)) {
      useSpell(send, gameId, {...others, x: x + shift.x, y: y + shift.y})
        .then(() => setActiveSpell(null));
    } else {
      console.warn('Failed validation activeSpell using', data);
    }
  };

  return (
    <Stack container direction="column" sx={{height: '100vh'}}>
      <CrossWardSpectatorPlayers/>
      {phase === 'PLAYING'
        ? <SpellZone activeSpell={activeSpell} setActiveSpell={setActiveSpell}/>
        : <></>}
      {phase !== 'WAITING'
        ? <HistoryZone historyHighlight={historyHighlight} setHistoryHighlight={setHistoryHighlight}/>
        : <></>}
      <PlayersZone/>
      <PanZoomProvider>
        <CrosswordCanvas
          onWordSubmit={handleWordSubmit}
          activeSpell={activeSpell}
          onSpellClick={handleSpellClick}
          selectedWord={selectedWord}
          setSelectedWord={setSelectedWord}
          historyHighlight={historyHighlight}
          setHistoryHighlight={setHistoryHighlight}
        />
      </PanZoomProvider>
      {isHost
        ? <CrossWardControl/>
        : <></>}
    </Stack>
  );
};
