import {useCallback, useEffect, useRef, useState} from 'react';

//TODO отрефачить этот нейрослоп
export const CrosswordCanvas = ({matrix, cellSize = 40, enableZoom = true}) => {
  const containerRef = useRef(null);
  const canvasRef = useRef(null);

  const [scale, setScale] = useState(1);
  const [offsetX, setOffsetX] = useState(0);
  const [offsetY, setOffsetY] = useState(0);

  const [isDragging, setIsDragging] = useState(false);
  const [dragStart, setDragStart] = useState({x: 0, y: 0});

  // --- Оптимизированная отрисовка: рисуем только видимые клетки ---
  const draw = useCallback((ctx, canvasWidth, canvasHeight) => {
    const rows = matrix.length;
    const cols = matrix[0]?.length || 0;

    // Очистка холста
    ctx.clearRect(0, 0, canvasWidth, canvasHeight);
    ctx.save();

    // Применяем трансформации (зум + панорамирование)
    ctx.translate(offsetX, offsetY);
    ctx.scale(scale, scale);

    // --- Вычисляем видимую область в локальных координатах (без трансформаций) ---
    // Углы видимой области в мировых координатах (с учётом трансформации)
    const topLeftX = -offsetX / scale;
    const topLeftY = -offsetY / scale;
    const bottomRightX = (canvasWidth - offsetX) / scale;
    const bottomRightY = (canvasHeight - offsetY) / scale;

    // Добавляем небольшой запас (одна клетка), чтобы избежать артефактов на границах
    const padding = cellSize;
    const visibleMinX = topLeftX - padding;
    const visibleMinY = topLeftY - padding;
    const visibleMaxX = bottomRightX + padding;
    const visibleMaxY = bottomRightY + padding;

    // Определяем диапазон строк и столбцов, попадающих в видимую область
    const startRow = Math.max(0, Math.floor(visibleMinY / cellSize));
    const endRow = Math.min(rows - 1, Math.floor(visibleMaxY / cellSize));
    const startCol = Math.max(0, Math.floor(visibleMinX / cellSize));
    const endCol = Math.min(cols - 1, Math.floor(visibleMaxX / cellSize));

    // Если ничего не видно (например, при сильном зуме), рисуем пустой холст
    if (startRow > endRow || startCol > endCol) {
      ctx.restore();
      return;
    }

    // --- Рисуем только видимые клетки ---
    for (let r = startRow; r <= endRow; r++) {
      const row = matrix[r];
      if (!row) continue;

      for (let c = startCol; c <= endCol; c++) {
        const value = row[c];
        const x = c * cellSize;
        const y = r * cellSize;

        // Фон клетки
        ctx.fillStyle = value ? '#ffffff' : '#222222';
        ctx.fillRect(x, y, cellSize, cellSize);

        // Рамка
        ctx.strokeStyle = '#333333';
        ctx.lineWidth = 1;
        ctx.strokeRect(x, y, cellSize, cellSize);

        // Буква
        if (value) {
          ctx.fillStyle = '#000000';
          const fontSize = Math.max(10, cellSize * 0.6);
          ctx.font = `bold ${fontSize}px Arial, sans-serif`;
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          ctx.fillText(value.toUpperCase(), x + cellSize / 2, y + cellSize / 2);
        }
      }
    }

    ctx.restore();
  }, [matrix, cellSize, offsetX, offsetY, scale]);

  // --- Перерисовка при изменении параметров ---
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

  // --- Центрирование ---
  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    const updateCenter = () => {
      const rect = container.getBoundingClientRect();
      const rows = matrix.length;
      const cols = matrix[0]?.length || 0;
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
  }, [matrix, cellSize]);

  // --- Обработчики для зума/пана ---
  const handleMouseDown = useCallback((e) => {
    if (!enableZoom) return;
    setIsDragging(true);
    setDragStart({x: e.clientX - offsetX, y: e.clientY - offsetY});
  }, [enableZoom, offsetX, offsetY]);

  const handleMouseMove = useCallback((e) => {
    if (!enableZoom || !isDragging) return;
    setOffsetX(e.clientX - dragStart.x);
    setOffsetY(e.clientY - dragStart.y);
  }, [enableZoom, isDragging, dragStart]);

  const handleMouseUp = useCallback(() => {
    setIsDragging(false);
  }, []);

  const handleWheel = useCallback((e) => {
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
  }, [enableZoom, offsetX, offsetY, scale]);

  const resetView = useCallback(() => {
    const container = containerRef.current;
    if (!container) return;
    const rect = container.getBoundingClientRect();
    const rows = matrix.length;
    const cols = matrix[0]?.length || 0;
    const totalWidth = cols * cellSize;
    const totalHeight = rows * cellSize;

    setOffsetX((rect.width - totalWidth) / 2);
    setOffsetY((rect.height - totalHeight) / 2);
    setScale(1);
  }, [matrix, cellSize]);

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
