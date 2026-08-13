import {useGame} from '@entities/game/lib/hooks';
import {usePanZoom} from '@shared/lib/pan-zoom/hooks';
import React, {useEffect, useRef} from 'react';
import {useCellHover} from '../lib/hooks/useCellHover';
import {useKeyboardEvents} from '../lib/hooks/useKeyboardEvents';
import {useMouseEvents} from '../lib/hooks/useMouseEvents';
import {useRenderer} from '../lib/hooks/useRenderer';
import {useWordSelection} from '../lib/hooks/useWordSelection';
import {useWordSelectionInput} from '../lib/hooks/useWordSelectionInput';
import {Canvas} from './Canvas';

export const CrosswordCanvas = ({cellSize = 40, onWordSubmit}) => {
  const canvasRef = useRef(null);
  const {containerRef, scale, offsetX, offsetY, resetView, isDragging} = usePanZoom();
  const {me: {color}, grid: {cells: cellsMap, size: {x: rows, y: cols}, shift}, words} = useGame();

  const {selectedWord, onCellClick} = useWordSelection();

  const {
    onLetterDown,
    onBackspaceDown,
    onSpaceDown,
    onEnterDown,
    manualLetters,
    activeCell
  } = useWordSelectionInput(selectedWord, words, cellsMap, shift, onWordSubmit);

  const {onKeyDown} = useKeyboardEvents(onLetterDown, onBackspaceDown, onSpaceDown, onEnterDown);

  const {hoveredCell, onHover} = useCellHover();

  const {onClick, onMouseMove, onMouseLeave} = useMouseEvents(
    onCellClick,
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
  );

  useEffect(() => {
    const rafId = requestAnimationFrame(() => {
      resetView(cols * cellSize, rows * cellSize);
    });
    return () => cancelAnimationFrame(rafId);
  }, []);

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
