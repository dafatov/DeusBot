import React, {useCallback, useEffect, useMemo, useRef} from 'react';
import {usePanZoom} from '../lib/hooks';

const DEFAULT_CELL_SIZE = 40;
const VISIBLE_PADDING = 40;

export const CrosswordCanvas = ({matrix, cellSize = DEFAULT_CELL_SIZE}) => {
  const canvasRef = useRef(null);
  const {containerRef, scale, offsetX, offsetY, resetView} = usePanZoom();

  const {cellsMap, rows, cols} = useMemo(() => {
    if (!Array.isArray(matrix) || matrix.length === 0) {
      return {cellsMap: new Map(), rows: 0, cols: 0};
    }

    // Находим минимумы и максимумы
    let minX = Infinity, maxX = -Infinity;
    let minY = Infinity, maxY = -Infinity;
    matrix.forEach(({x, y}) => {
      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      if (y < minY) minY = y;
      if (y > maxY) maxY = y;
    });

    // Сдвиг так, чтобы минимальная координата стала 0
    const shiftX = minX; // или minX - 1, если нужны строго положительные
    const shiftY = minY;

    const map = new Map();
    matrix.forEach(({x, y, letter}) => {
      const newX = x - shiftX; // теперь newX >= 0
      const newY = y - shiftY; // теперь newY >= 0
      map.set(`${newX},${newY}`, letter);
    });

    // Размеры сетки (количество строк и столбцов)
    const rows = maxY - minY + 1;
    const cols = maxX - minX + 1;

    return {cellsMap: map, rows, cols};
  }, [matrix]);

  const drawCanvas = useCallback((ctx, canvasWidth, canvasHeight) => {
    ctx.clearRect(0, 0, canvasWidth, canvasHeight);
    ctx.save();

    ctx.translate(offsetX, offsetY);
    ctx.scale(scale, scale);

    const topLeftX = -offsetX / scale;
    const topLeftY = -offsetY / scale;
    const bottomRightX = (canvasWidth - offsetX) / scale;
    const bottomRightY = (canvasHeight - offsetY) / scale;

    const visibleMinX = topLeftX - VISIBLE_PADDING;
    const visibleMinY = topLeftY - VISIBLE_PADDING;
    const visibleMaxX = bottomRightX + VISIBLE_PADDING;
    const visibleMaxY = bottomRightY + VISIBLE_PADDING;

    const startRow = Math.max(0, Math.floor(visibleMinY / cellSize));
    const endRow = Math.min(rows - 1, Math.floor(visibleMaxY / cellSize));
    const startCol = Math.max(0, Math.floor(visibleMinX / cellSize));
    const endCol = Math.min(cols - 1, Math.floor(visibleMaxX / cellSize));

    if (startRow > endRow || startCol > endCol || rows === 0 || cols === 0) {
      ctx.restore();
      return;
    }

    for (let r = startRow; r <= endRow; r++) {
      for (let c = startCol; c <= endCol; c++) {
        const x = c * cellSize;
        const y = r * cellSize;
        const key = `${c},${r}`;
        const letter = cellsMap.get(key);

        ctx.fillStyle = letter ? '#ffffff' : '#222222';
        ctx.fillRect(x, y, cellSize, cellSize);
        ctx.strokeStyle = '#333333';
        ctx.lineWidth = 1;
        ctx.strokeRect(x, y, cellSize, cellSize);

        if (letter) {
          ctx.fillStyle = '#000000';
          ctx.font = `bold ${(Math.max(10, cellSize * 0.6))}px Arial, sans-serif`;
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          ctx.fillText(letter.toUpperCase(), x + cellSize / 2, y + cellSize / 2);
        }
      }
    }

    ctx.restore();
  }, [cellsMap, rows, cols, cellSize, offsetX, offsetY, scale]);

  const centerGrid = useCallback(() => {
    resetView(cols * cellSize, rows * cellSize);
  }, [cols, rows, cellSize, resetView]);

  useEffect(() => {
    const canvas = canvasRef.current;
    const container = containerRef.current;

    if (!canvas || !container) {
      console.warn('Canvas or container not found');
      return;
    }

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
    drawCanvas(ctx, logicalWidth, logicalHeight);
  }, [drawCanvas, containerRef]);

  useEffect(() => {
    const handleResize = () => centerGrid();

    handleResize();
    window.addEventListener('resize', handleResize);

    return () => window.removeEventListener('resize', handleResize);
  }, [centerGrid]);

  return (
    <canvas
      ref={canvasRef}
      style={{
        display: 'block',
        width: '100%',
        height: '100%',
      }}
    />
  );
};
