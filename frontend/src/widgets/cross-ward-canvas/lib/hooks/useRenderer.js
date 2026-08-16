import {alpha} from '@mui/material';
import {useCallback, useEffect} from 'react';
import {getCellBackgroundColor} from '../utils/getCellBackgroundColor';
import {getCellEdgeColor} from '../utils/getCellEdgeColor';
import {getVisibleRange} from '../utils/getVisibleRange';
import {getWordEndpoints} from '../utils/getWordEndpoints';

const NEUTRAL_WORD_COLOR = '#000000';

const drawLine = (ctx, x1, y1, x2, y2, color) => {
  ctx.strokeStyle = color;
  ctx.beginPath();
  ctx.moveTo(x1, y1);
  ctx.lineTo(x2, y2);
  ctx.stroke();
};

export const useRenderer = (
  canvasRef,
  containerRef,
  offset,
  scale,
  size,
  cellSize,
  cellsMap,
  words,
  selectedWord,
  hoveredCell,
  color,
  shift,
  manualLetters,
  activeCell,
  areaSpell,
  historyVisible,
) => {
  const drawWordSelection = useCallback((ctx, wordId) => {
    const word = words?.[wordId];

    if (!word) return;

    const endpoints = getWordEndpoints(word.cells);

    ctx.save();
    ctx.strokeStyle = word.border ?? NEUTRAL_WORD_COLOR;
    ctx.lineWidth = 3;
    ctx.strokeRect(
      (endpoints.first.x - shift.x) * cellSize,
      (endpoints.first.y - shift.y) * cellSize,
      endpoints.size.x * cellSize,
      endpoints.size.y * cellSize
    );
    ctx.restore();
  }, [words, shift, cellSize]);

  const drawHoveredCell = useCallback(ctx => {
    if (!hoveredCell) return;

    ctx.save();
    ctx.strokeStyle = color;
    ctx.lineWidth = 2;
    ctx.strokeRect(
      hoveredCell.x * cellSize,
      hoveredCell.y * cellSize,
      cellSize,
      cellSize
    );
    ctx.restore();
  }, [hoveredCell, color, cellSize]);

  const drawArea = useCallback((ctx, area) => {
    if (!area?.radius) return;

    ctx.save();
    ctx.strokeStyle = alpha(area.color, 0.25);
    ctx.lineWidth = 2;
    ctx.strokeRect(
      (area.x - area.radius) * cellSize,
      (area.y - area.radius) * cellSize,
      (2 * area.radius + 1) * cellSize,
      (2 * area.radius + 1) * cellSize
    );
    ctx.restore();
  }, []);

  const drawSymbol = useCallback((ctx, x, y, symbol, color) => {
    ctx.save();
    ctx.fillStyle = color;
    ctx.font = `bold ${Math.max(10, cellSize * 0.6)}px Arial, sans-serif`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(symbol?.toUpperCase(), x * cellSize + cellSize / 2, y * cellSize + cellSize / 2);
    ctx.restore();
  }, [cellSize]);

  const drawCarriage = useCallback(ctx => {
    if (!activeCell) return;

    drawSymbol(ctx, activeCell.x, activeCell.y, '>', color);
  }, [activeCell, color, drawSymbol]);

  const drawLetter = useCallback((ctx, current, j, i) => {
    const manualLetter = manualLetters?.[`${j},${i}`];

    if (!current && !manualLetter) return;

    drawSymbol(ctx, j, i, manualLetter ?? current.letter ?? '', manualLetter ? color : NEUTRAL_WORD_COLOR);
  }, [manualLetters, color, drawSymbol]);

  const drawBackground = useCallback((ctx, current, j, i) => {
    ctx.save();
    ctx.fillStyle = getCellBackgroundColor(current, words);
    ctx.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
    ctx.restore();
  }, [cellSize, words]);

  const drawHistory = useCallback(ctx => {
    if (!historyVisible) return;

    switch (historyVisible.type) {
      case 'area':
        drawArea(ctx, historyVisible);
        return;
      case 'word':
        drawWordSelection(ctx, historyVisible?.wordId);
        return;
    }
  }, [historyVisible, drawArea, drawWordSelection]);

  const createEdges = useCallback((x, y, isFirstCol, isLastCol, right, isFirstRow, isLastRow, bottom) => [
    {isV: true, x1: x, y1: y, x2: x, y2: y + cellSize, draw: isFirstCol, neighbor: null},
    {isV: true, x1: x + cellSize, y1: y, x2: x + cellSize, y2: y + cellSize, draw: true, neighbor: isLastCol ? null : right},
    {isV: false, x1: x, y1: y, x2: x + cellSize, y2: y, draw: isFirstRow, neighbor: null},
    {isV: false, x1: x, y1: y + cellSize, x2: x + cellSize, y2: y + cellSize, draw: true, neighbor: isLastRow ? null : bottom}
  ], [cellSize]);

  const drawCell = useCallback((ctx, j, i, isFirstCol, isFirstRow, isLastCol, isLastRow) => {
    const x = j * cellSize;
    const y = i * cellSize;
    const current = cellsMap.get(`${j},${i}`);
    const right = cellsMap.get(`${j + 1},${i}`);
    const bottom = cellsMap.get(`${j},${i + 1}`);

    drawBackground(ctx, current, j, i);
    drawLetter(ctx, current, j, i);

    createEdges(x, y, isFirstCol, isLastCol, right, isFirstRow, isLastRow, bottom).forEach(({isV, x1, y1, x2, y2, draw, neighbor}) => {
      if (!draw) return;

      const colorResult = getCellEdgeColor(isV, current, neighbor, isV ? !!bottom : !!right);
      let color;

      if (Array.isArray(colorResult)) {
        const gradient = ctx.createLinearGradient(x1, y1, x2, y2);
        const c0 = words?.[colorResult[0]]?.border ?? NEUTRAL_WORD_COLOR;
        const c1 = words?.[colorResult[1]]?.border ?? NEUTRAL_WORD_COLOR;

        gradient.addColorStop(0, c0);
        gradient.addColorStop(0.25, c0);
        gradient.addColorStop(0.75, c1);
        gradient.addColorStop(1, c1);
        color = gradient;
      } else if (colorResult) {
        color = words?.[colorResult]?.border ?? NEUTRAL_WORD_COLOR;
      } else {
        color = '#00000011';
      }

      drawLine(ctx, x1, y1, x2, y2, color);
    });
  }, [cellSize, cellsMap, words, drawLetter]);

  const drawAreaSpell = useCallback(ctx => {
    if (!areaSpell || !hoveredCell) {
      return;
    }

    drawArea(ctx, {x: hoveredCell.x, y: hoveredCell.y, radius: areaSpell.radius, color});
  }, [areaSpell, hoveredCell, drawArea]);

  const draw = useCallback((ctx, width, height) => {
    ctx.clearRect(0, 0, width, height);
    ctx.save();

    ctx.translate(offset.x, offset.y);
    ctx.scale(scale, scale);

    const {startRow, endRow, startCol, endCol} = getVisibleRange(offset, scale, width, height, cellSize, size);

    if (startRow > endRow || startCol > endCol || size.x === 0 || size.y === 0) {
      ctx.restore();
      return;
    }

    for (let i = startRow; i <= endRow; i++) {
      for (let j = startCol; j <= endCol; j++) {
        drawCell(ctx, j, i, j === startCol, i === startRow, j === endCol, i === endRow);
      }
    }

    drawWordSelection(ctx, selectedWord);
    drawHoveredCell(ctx);
    drawCarriage(ctx);
    drawAreaSpell(ctx);
    drawHistory(ctx);

    ctx.restore();
  }, [offset, scale, size, cellSize, selectedWord, drawCell, drawWordSelection, drawHoveredCell, drawCarriage, drawAreaSpell, drawHistory]);

  useEffect(() => {
    const canvas = canvasRef.current;
    const container = containerRef.current;
    if (!canvas || !container) return;

    const ctx = canvas.getContext('2d');
    const rect = container.getBoundingClientRect();
    const dpr = window.devicePixelRatio || 1;
    const logicalWidth = rect.width;
    const logicalHeight = rect.height;

    canvas.width = logicalWidth * dpr;
    canvas.height = logicalHeight * dpr;
    canvas.style.width = `${logicalWidth}px`;
    canvas.style.height = `${logicalHeight}px`;

    ctx.scale(dpr, dpr);
    draw(ctx, logicalWidth, logicalHeight);
  }, [draw, containerRef, canvasRef]);
};
