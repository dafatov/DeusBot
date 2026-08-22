import {useGame} from '@entities/game/lib/hooks';
import {usePanZoom} from '@shared/lib/pan-zoom/hooks';
import React, {useEffect, useRef} from 'react';
import {useAreaSpell} from '../lib/hooks/useAreaSpell';
import {useCellHover} from '../lib/hooks/useCellHover';
import {useKeyboardEvents} from '../lib/hooks/useKeyboardEvents';
import {useMouseEvents} from '../lib/hooks/useMouseEvents';
import {useRenderer} from '../lib/hooks/useRenderer';
import {useWordSelection} from '../lib/hooks/useWordSelection';
import {useWordSelectionInput} from '../lib/hooks/useWordSelectionInput';
import {getWordEndpoints} from '../lib/utils/getWordEndpoints';
import {Canvas} from './Canvas';

//TODO отображать какими буквами клетка точно не является
export const CrosswordCanvas = ({cellSize = 40, onWordSubmit, onSpellClick, activeSpell, selectedWord, setSelectedWord, historyVisible}) => {
  const canvasRef = useRef(null);
  const {containerRef, scale, offsetX, offsetY, resetView, moveToView, isDragging} = usePanZoom();
  const {me: {color}, grid: {cells: cellsMap, size: {x: rows, y: cols}, shift}, words, startedAt} = useGame();

  const {onCellClick: handleWordSelectionCellClick} = useWordSelection(setSelectedWord);

  const {onCellClick: handleSpellCellClick} = useAreaSpell(onSpellClick);

  const {
    onLetterDown,
    onBackspaceDown,
    onSpaceDown,
    onEnterDown,
    manualLetters,
    activeCell,
  } = useWordSelectionInput(startedAt, selectedWord, words, cellsMap, shift, onWordSubmit);

  const {onKeyDown} = useKeyboardEvents(onLetterDown, onBackspaceDown, onSpaceDown, onEnterDown);

  const {hoveredCell, onHover} = useCellHover();

  const {onClick, onMouseMove, onMouseLeave} = useMouseEvents(
    activeSpell ? handleSpellCellClick : handleWordSelectionCellClick,
    onHover,
    canvasRef,
    {x: offsetX, y: offsetY},
    scale,
    cellSize,
    {x: rows, y: cols},
    cellsMap
  );

  useRenderer(
    canvasRef,
    containerRef,
    {x: offsetX, y: offsetY},
    scale,
    {x: rows, y: cols},
    cellSize,
    cellsMap,
    words,
    selectedWord,
    hoveredCell,
    color,
    shift,
    manualLetters,
    activeCell,
    activeSpell,
    historyVisible,
  );

  useEffect(() => {
    const rafId = requestAnimationFrame(() => {
      resetView(cols * cellSize, rows * cellSize);
    });
    return () => cancelAnimationFrame(rafId);
  }, []);

  useEffect(() => {
    if (!historyVisible) {
      return;
    }

    let x = historyVisible?.x;
    let y = historyVisible?.y;
    if (historyVisible.wordId) {
      const {first, last} = getWordEndpoints(words[historyVisible.wordId].cells);

      x = (first.x + last.x) / 2 - shift.x;
      y = (first.y + last.y) / 2 - shift.y;
    }

    if (!x || !y) {
      return;
    }

    moveToView(x * cellSize, y * cellSize);
  }, [historyVisible, moveToView, words, shift]);

  return (
    <Canvas
      ref={canvasRef}
      onClick={onClick}
      onMouseMove={onMouseMove}
      onMouseLeave={onMouseLeave}
      onKeyDown={onKeyDown}
      cursor={!isDragging && hoveredCell ? 'pointer' : 'inherit'}
    />
  );
};
