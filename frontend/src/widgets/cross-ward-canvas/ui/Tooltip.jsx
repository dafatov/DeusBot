import {useGame} from '@entities/game/lib/hooks';
import {HistoryItem} from '@entities/game/ui/crossward/HistoryItem';
import {buildHistoryItems} from '@entities/game/utils/crossward/buildHistoryItemProps';
import {Divider, List, Paper} from '@mui/material';
import {usePanZoom} from '@shared/lib/pan-zoom/hooks';
import React, {useEffect, useMemo, useRef} from 'react';
import {getWordDirection, getWordId} from '../lib/utils/getWordInfo';

const styles = t => ({
  flex: 1,
  overflowY: 'auto',
  minHeight: 0,
  maxHeight: '100px',
  '&::-webkit-scrollbar': {
    width: t.spacing(0.5),
  },
  '&::-webkit-scrollbar-thumb': {
    background: t.palette.primary.main,
  },
  '&::-webkit-scrollbar-thumb:hover': {
    background: t.palette.primary.dark,
  },
});

export const Tooltip = ({data, cellSize, onMouseEnter, onMouseLeave, historyHighlight, setHistoryHighlight}) => {
  const tooltipRef = useRef(null);
  const {containerRef, scale, offsetX, offsetY} = usePanZoom();
  const {history: historyMap, words, grid: {shift}, letterTags, findPlayer} = useGame();

  const position = useMemo(() => {
    const container = containerRef.current;
    if (!container || !data) {
      return {left: 0, top: 0};
    }

    const rect = container.getBoundingClientRect();
    const x = data.x * cellSize * scale + offsetX;
    const y = data.y * cellSize * scale + offsetY;

    return {
      left: rect.left + x,
      top: rect.top + y,
      transform: getWordDirection(data) === 'HORIZONTAL' ? 'translate(0, calc(-100%))' : `translate(calc(${cellSize * scale}px), 0)`,
    };
  }, [data, containerRef, offsetX, offsetY, scale, cellSize]);

  const history = useMemo(() => {
    if (!data) {
      return null;
    }

    const wordId = getWordId(data);
    const word = words?.[wordId];
    const cellActions = data?.cell?.history?.map(id => historyMap.get(id)) ?? [];
    const wordActions = word?.history?.map(id => historyMap.get(id)) ?? [];
    const cellItems = buildHistoryItems(
      setHistoryHighlight,
      historyHighlight,
      findPlayer,
      shift,
      letterTags,
      cellActions
    );
    const wordItems = buildHistoryItems(
      setHistoryHighlight,
      historyHighlight,
      findPlayer,
      shift,
      letterTags,
      wordActions
    );

    if (!cellItems.length && !wordItems.length) {
      return null;
    }

    return {cell: cellItems, word: wordItems};
  }, [data, historyMap, words, historyHighlight, setHistoryHighlight, findPlayer, shift, letterTags]);

  useEffect(() => {
    const handleEvent = e => {
      if (!tooltipRef.current?.contains(e.target)) {
        return;
      }

      e.stopPropagation();
    };

    document.addEventListener('wheel', handleEvent, {passive: false, capture: true});
    return () => {
      document.removeEventListener('wheel', handleEvent, {capture: true});
    };
  }, []);

  if (!history) {
    return null;
  }

  return (
    <Paper
      ref={tooltipRef}
      elevation={3}
      sx={{
        position: 'fixed',
        left: position.left,
        top: position.top,
        transform: position.transform,
        zIndex: 1000,
        pointerEvents: 'auto',
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden',
        cursor: 'default',
      }}
      onMouseEnter={onMouseEnter}
      onMouseLeave={onMouseLeave}
      onMouseDown={(e) => e.stopPropagation()}
    >
      {history.word?.length ? <List spacing={0.5} sx={styles}>
        {history.word.map(historyItem => (
          <HistoryItem {...historyItem}/>
        ))}
      </List> : null}

      {history.word.length > 0 && history.cell.length > 0 ? <Divider flexItem/> : null}

      {history.cell?.length ? <List title="История ячейки" spacing={0.5} sx={styles}>
        {history.cell.map(historyItem => (
          <HistoryItem {...historyItem}/>
        ))}
      </List> : null}
    </Paper>
  );
};
