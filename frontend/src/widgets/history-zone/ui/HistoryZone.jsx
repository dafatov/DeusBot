import {useGame} from '@entities/game/lib/hooks';
import {HistoryItem} from '@entities/game/ui/crossward/HistoryItem';
import {buildHistoryItems} from '@entities/game/utils/crossward/buildHistoryItemProps';
import {List, Paper} from '@mui/material';
import React, {useMemo} from 'react';

export const HistoryZone = ({historyHighlight, setHistoryHighlight}) => {
  const {grid: {shift}, history: historyMap, findPlayer, letterTags} = useGame();

  const history = useMemo(() => buildHistoryItems(
    setHistoryHighlight,
    historyHighlight,
    findPlayer,
    shift,
    letterTags,
    Array.from(historyMap.values())
  ), [setHistoryHighlight, setHistoryHighlight, findPlayer, letterTags, shift, historyMap, historyHighlight]);

  return (
    <Paper sx={{
      position: 'absolute',
      top: '50%',
      transform: 'translateY(-50%)',
      left: 16,
      zIndex: 2,
      minWidth: '200px',
      opacity: 0.8,
      minHeight: '50vh',
      maxHeight: '50vh',
      overflow: 'auto',
    }}>
      <List>
        {history.map(historyItem => (
          <HistoryItem {...historyItem}/>
        ))}
      </List>
    </Paper>
  );
};
