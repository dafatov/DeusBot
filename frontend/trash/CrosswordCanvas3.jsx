import {useCallback, useEffect, useMemo, useRef, useState} from 'react';

export const CrosswordCanvas3 = ({matrix, cellSize = 40}, enableZoom = true) => {
  const containerRef = useRef(null);
  const canvasRef = useRef(null);

  const [scale, setScale] = useState(1);
  const [offsetX, setOffsetX] = useState(0);
  const [offsetY, setOffsetY] = useState(0);
  const [isDragging, setIsDragging] = useState(false);
  const [dragStart, setDragStart] = useState({x: 0, y: 0});

  // --- Преобразуем список клеток в карту и вычисляем размеры сетки ---
  const {cellsMap, rows, cols} = useMemo(() => {
    if (!Array.isArray(matrix) || matrix.length === 0) {
      return {cellsMap: new Map(), rows: 0, cols: 0};
    }
    const map = new Map();
    let maxX = 0;
    let maxY = 0;
    matrix.forEach(({x, y, letter}) => {
      map.set(`${x},${y}`, letter);
      if (x > maxX) maxX = x;
      if (y > maxY) maxY = y;
    });
    return {cellsMap: map, rows: maxY + 1, cols: maxX + 1};
  }, [matrix]);

  // --- Отрисовка (используем вычисленные rows/cols и cellsMap) ---
  const draw = useCallback(
    (ctx, canvasWidth, canvasHeight) => {
      ctx.clearRect(0, 0, canvasWidth, canvasHeight);
      ctx.save();

      ctx.translate(offsetX, offsetY);
      ctx.scale(scale, scale);

      // Видимая область
      const topLeftX = -offsetX / scale;
      const topLeftY = -offsetY / scale;
      const bottomRightX = (canvasWidth - offsetX) / scale;
      const bottomRightY = (canvasHeight - offsetY) / scale;

      const padding = cellSize;
      const visibleMinX = topLeftX - padding;
      const visibleMinY = topLeftY - padding;
      const visibleMaxX = bottomRightX + padding;
      const visibleMaxY = bottomRightY + padding;

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
            const fontSize = Math.max(10, cellSize * 0.6);
            ctx.font = `bold ${fontSize}px Arial, sans-serif`;
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(letter.toUpperCase(), x + cellSize / 2, y + cellSize / 2);
          }
        }
      }

      ctx.restore();
    },
    [cellsMap, rows, cols, cellSize, offsetX, offsetY, scale]
  );

  // --- Перерисовка при изменении ---
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
    canvas.style.width = logicalWidth + 'px';
    canvas.style.height = logicalHeight + 'px';

    ctx.scale(dpr, dpr);
    draw(ctx, logicalWidth, logicalHeight);
  }, [draw]);

  // --- Центрирование (используем вычисленные rows/cols) ---
  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    const updateCenter = () => {
      const rect = container.getBoundingClientRect();
      const totalWidth = cols * cellSize;
      const totalHeight = rows * cellSize;

      setOffsetX((rect.width - totalWidth) / 2);
      setOffsetY((rect.height - totalHeight) / 2);
      setScale(1);
    };

    updateCenter();

    const handleResize = () => updateCenter();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [rows, cols, cellSize]);

  // --- Обработчики мыши и колеса (без изменений) ---
  const handleMouseDown = useCallback(
    (e) => {
      if (!enableZoom) return;
      setIsDragging(true);
      setDragStart({x: e.clientX - offsetX, y: e.clientY - offsetY});
    },
    [enableZoom, offsetX, offsetY]
  );

  const handleMouseMove = useCallback(
    (e) => {
      if (!enableZoom || !isDragging) return;
      setOffsetX(e.clientX - dragStart.x);
      setOffsetY(e.clientY - dragStart.y);
    },
    [enableZoom, isDragging, dragStart]
  );

  const handleMouseUp = useCallback(() => {
    setIsDragging(false);
  }, []);

  const handleWheel = useCallback(
    (e) => {
      if (!enableZoom) return;
      e.preventDefault();

      const container = containerRef.current;
      if (!container) return;
      const rect = container.getBoundingClientRect();

      const mouseX = e.clientX - rect.left;
      const mouseY = e.clientY - rect.top;

      const worldX = (mouseX - offsetX) / scale;
      const worldY = (mouseY - offsetY) / scale;

      const delta = e.deltaY > 0 ? 0.9 : 1.1;
      const newScale = Math.min(Math.max(scale * delta, 0.2), 5);

      setScale(newScale);
      setOffsetX(mouseX - worldX * newScale);
      setOffsetY(mouseY - worldY * newScale);
    },
    [enableZoom, offsetX, offsetY, scale]
  );

  const resetView = useCallback(() => {
    const container = containerRef.current;
    if (!container) return;
    const rect = container.getBoundingClientRect();
    const totalWidth = cols * cellSize;
    const totalHeight = rows * cellSize;

    setOffsetX((rect.width - totalWidth) / 2);
    setOffsetY((rect.height - totalHeight) / 2);
    setScale(1);
  }, [rows, cols, cellSize]);

  // --- JSX (без изменений) ---
  return (
    <div
      ref={containerRef}
      style={{
        width: '100%',
        height: '600px',
        overflow: 'hidden',
        position: 'relative',
        backgroundColor: '#f0f0f0',
        cursor: enableZoom ? (isDragging ? 'grabbing' : 'grab') : 'default',
      }}
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
      onMouseLeave={handleMouseUp}
      onWheel={handleWheel}
    >
      <canvas
        ref={canvasRef}
        style={{
          display: 'block',
          width: '100%',
          height: '100%',
        }}
      />
      {enableZoom && (
        <div style={{position: 'absolute', bottom: 16, right: 16, display: 'flex', gap: 8}}>
          <button onClick={() => setScale(prev => Math.min(prev * 1.2, 5))}>➕</button>
          <button onClick={() => setScale(prev => Math.max(prev / 1.2, 0.2))}>➖</button>
          <button onClick={resetView}>⟲</button>
        </div>
      )}
    </div>
  );
};
