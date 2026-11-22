import {useGame} from '@entities/game/lib/hooks';
import {usePanZoom} from '@shared/lib/pan-zoom/hooks';
import React, {useEffect, useRef} from 'react';
import {useCellHover} from '../lib/hooks/useCellHover';
import {useKeyboardEvents} from '../lib/hooks/useKeyboardEvents';
import {useMouseEvents} from '../lib/hooks/useMouseEvents';
import {useRenderer} from '../lib/hooks/useRenderer';
import {useSpell} from '../lib/hooks/useSpell';
import {useTooltip} from '../lib/hooks/useTooltip';
import {useWordSelection} from '../lib/hooks/useWordSelection';
import {useWordSelectionInput} from '../lib/hooks/useWordSelectionInput';
import {getWordEndpoints} from '../lib/utils/getWordEndpoints';
import {Canvas} from './Canvas';
import {Tooltip} from './Tooltip';

export const CrosswordCanvas = ({
                                  cellSize = 40,
                                  onWordSubmit,
                                  onSpellClick,
                                  activeSpell,
                                  selectedWord,
                                  setSelectedWord,
                                  historyHighlight,
                                  setHistoryHighlight
                                }) => {
  const canvasRef = useRef(null);
  const {containerRef, scale, offsetX, offsetY, resetView, moveToView, isDragging} = usePanZoom();
  const {me: {color}, grid: {cells: cellsMap, size: {x: rows, y: cols}, shift}, words, startedAt} = useGame();

  const {onCellClick: handleWordSelectionCellClick} = useWordSelection(setSelectedWord);

  const {onCellClick: handleSpellCellClick} = useSpell(onSpellClick);

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

  const {tooltipData, onTooltipEnter, onTooltipLeave} = useTooltip(hoveredCell, activeSpell, isDragging);

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
    historyHighlight,
  );

  useEffect(() => {
    const rafId = requestAnimationFrame(() => {
      resetView(cols * cellSize, rows * cellSize);
    });
    return () => cancelAnimationFrame(rafId);
  }, []);

  useEffect(() => {
    if (!historyHighlight) {
      return;
    }

    let x = historyHighlight?.x;
    let y = historyHighlight?.y;
    if (historyHighlight.wordId) {
      const {first, last} = getWordEndpoints(words[historyHighlight.wordId].cells);

      x = (first.x + last.x) / 2 - shift.x;
      y = (first.y + last.y) / 2 - shift.y;
    }

    if (!x || !y) {
      return;
    }

    const worldX = x * cellSize;
    const worldY = y * cellSize;
    const rect = containerRef.current?.getBoundingClientRect();

    if (!rect) {
      return;
    }

    if (worldX * scale + offsetX >= 0
      && worldX * scale + offsetX <= rect.width
      && worldY * scale + offsetY >= 0
      && worldY * scale + offsetY <= rect.height) {
      return;
    }

    moveToView(worldX, worldY);
  }, [historyHighlight, moveToView, words, shift, containerRef, cellSize]);

  return (
    <>
      <Canvas
        ref={canvasRef}
        onClick={onClick}
        onMouseMove={onMouseMove}
        onMouseLeave={onMouseLeave}
        onKeyDown={onKeyDown}
        cursor={!isDragging && hoveredCell ? 'pointer' : 'inherit'}
      />
      <Tooltip
        data={tooltipData}
        cellSize={cellSize}
        onMouseEnter={onTooltipEnter}
        onMouseLeave={onTooltipLeave}
        historyHighlight={historyHighlight}
        setHistoryHighlight={setHistoryHighlight}
      />
    </>
  );
};
