import {useGame} from '@entities/game/lib/hooks';
import {usePanZoom} from '@shared/lib/pan-zoom/hooks';
import React, {useEffect, useRef} from 'react';
import {useCellHover} from '../lib/hooks/useCellHover';
import {useMouseEvents} from '../lib/hooks/useMouseEvents';
import {useRenderer} from '../lib/hooks/useRenderer';
import {useWordSelection} from '../lib/hooks/useWordSelection';
import {Canvas} from './Canvas';

export const CrosswordCanvas = ({cellSize = 40}) => {
  const canvasRef = useRef(null);
  const {containerRef, scale, offsetX, offsetY, resetView, isDragging} = usePanZoom();
  const {me: {color}, grid: {cells: cellsMap, size: {x: rows, y: cols}, shift: {x: shiftX, y: shiftY}}, words} = useGame();

  const {selectedWord, onCellClick} = useWordSelection();

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
    {x: shiftX, y: shiftY}
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
      cursor={!isDragging && hoveredCell ? 'pointer' : 'inherit'}
    />
  );
};
