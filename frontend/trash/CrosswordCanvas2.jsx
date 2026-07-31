import {useCallback, useEffect, useRef, useState} from 'react';

//TODO вытащить функционал ввода букв (очень классно)
export const CrosswordCanvas2 = ({
                                   matrix,               // двумерный массив (буквы или null)
                                   words = [],           // массив слов: [{ word, row, col, direction }]
                                   cellSize = 40,
                                   enableZoom = true,
                                 }) => {
  const containerRef = useRef(null);
  const canvasRef = useRef(null);

  // --- Трансформации (зум/пан) ---
  const [scale, setScale] = useState(1);
  const [offsetX, setOffsetX] = useState(0);
  const [offsetY, setOffsetY] = useState(0);

  // --- Состояние для перетаскивания ---
  const [isDragging, setIsDragging] = useState(false);
  const [dragStart, setDragStart] = useState({x: 0, y: 0});

  // --- Состояние ввода и выделения ---
  const [userFilled, setUserFilled] = useState(() =>
    matrix.map(row => [...row]) // копия матрицы, будем менять только null → буква
  );
  const [selectedCell, setSelectedCell] = useState(null); // { row, col } | null
  const [highlightedCells, setHighlightedCells] = useState(new Set());

  // --- Поиск слов, содержащих клетку ---
  const findWordsForCell = useCallback((row, col) => {
    const result = [];
    for (const w of words) {
      const {row: r, col: c, direction, word} = w;
      if (direction === 'across') {
        if (row === r && col >= c && col < c + word.length) {
          result.push(w);
        }
      } else if (direction === 'down') {
        if (col === c && row >= r && row < r + word.length) {
          result.push(w);
        }
      }
    }
    return result;
  }, [words]);

  // --- Получение всех клеток слова ---
  const getCellsOfWord = useCallback((wordObj) => {
    const {row, col, direction, word} = wordObj;
    const cells = [];
    for (let i = 0; i < word.length; i++) {
      if (direction === 'across') cells.push({row, col: col + i});
      else cells.push({row: row + i, col});
    }
    return cells;
  }, []);

  // --- Обработчик клика по холсту ---
  const handleCanvasClick = useCallback((e) => {
    const container = containerRef.current;
    if (!container) return;
    const rect = container.getBoundingClientRect();

    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;

    // Переводим в мировые координаты (с учётом трансформаций)
    const worldX = (mouseX - offsetX) / scale;
    const worldY = (mouseY - offsetY) / scale;

    const col = Math.floor(worldX / cellSize);
    const row = Math.floor(worldY / cellSize);

    // Проверяем, что клик в пределах сетки
    if (row < 0 || row >= matrix.length || col < 0 || col >= (matrix[0]?.length || 0)) {
      // Клик вне сетки – сбрасываем выделение
      setSelectedCell(null);
      setHighlightedCells(new Set());
      return;
    }

    // Если клетка уже выбрана – сбрасываем выделение
    if (selectedCell && selectedCell.row === row && selectedCell.col === col) {
      setSelectedCell(null);
      setHighlightedCells(new Set());
      return;
    }

    // Проверяем, принадлежит ли клетка какому-либо слову
    const foundWords = findWordsForCell(row, col);
    if (foundWords.length > 0) {
      // Подсвечиваем все клетки всех найденных слов
      const newHighlight = new Set();
      for (const w of foundWords) {
        const cells = getCellsOfWord(w);
        for (const c of cells) {
          newHighlight.add(`${c.row},${c.col}`);
        }
      }
      setHighlightedCells(newHighlight);
      setSelectedCell({row, col});
    } else {
      // Если клетка не принадлежит слову, но может быть пустой – выделяем для ввода
      setHighlightedCells(new Set());
      setSelectedCell({row, col});
    }
  }, [matrix, offsetX, offsetY, scale, cellSize, selectedCell, findWordsForCell, getCellsOfWord]);

  // --- Обработка клавиатурного ввода ---
  useEffect(() => {
    const handleKeyDown = (e) => {
      // Игнорируем, если фокус в поле ввода (input/textarea)
      if (document.activeElement?.tagName === 'INPUT' || document.activeElement?.tagName === 'TEXTAREA') {
        return;
      }

      if (!selectedCell) return;
      const {row, col} = selectedCell;

      // Обработка Backspace / Delete – удаляем букву (если она введена пользователем)
      if (e.key === 'Backspace' || e.key === 'Delete') {
        e.preventDefault();
        // Проверяем, что клетка не является фиксированной (т.е. изначально была null)
        if (matrix[row][col] === null) {
          setUserFilled(prev => {
            const newFilled = prev.map(r => [...r]);
            newFilled[row][col] = null;
            return newFilled;
          });
          // Можно автоматически перейти на предыдущую клетку в слове? – не обязательно
        }
        return;
      }

      // Ввод буквы (только латиница или кириллица, но упростим – разрешим все печатные символы)
      if (e.key.length === 1 && /[a-zA-Zа-яА-ЯёЁ]/.test(e.key)) {
        e.preventDefault();
        // Проверяем, что клетка пустая (null) или уже заполнена пользователем (можно перезаписывать)
        // Но если это фиксированная буква (была в matrix), не даём менять
        if (matrix[row][col] !== null) {
          // Фиксированная буква – игнорируем
          return;
        }
        const letter = e.key.toUpperCase();
        setUserFilled(prev => {
          const newFilled = prev.map(r => [...r]);
          newFilled[row][col] = letter;
          return newFilled;
        });

        // Автоматический переход на следующую клетку в том же слове (если есть)
        // Найдём первое слово, в котором участвует клетка, и попробуем перейти вперёд
        const foundWords = findWordsForCell(row, col);
        if (foundWords.length > 0) {
          const wordObj = foundWords[0]; // берём первое слово (горизонтальное приоритетнее)
          const cells = getCellsOfWord(wordObj);
          const currentIndex = cells.findIndex(c => c.row === row && c.col === col);
          if (currentIndex !== -1 && currentIndex < cells.length - 1) {
            const nextCell = cells[currentIndex + 1];
            // Проверяем, что следующая клетка существует и не фиксированная (или пустая)
            if (nextCell.row < matrix.length && nextCell.col < matrix[0].length) {
              // Перемещаем выделение
              setSelectedCell({row: nextCell.row, col: nextCell.col});
              // Обновляем подсветку (можно оставить ту же)
              // Можно также подсветить все слова, содержащие новую клетку
              const newFound = findWordsForCell(nextCell.row, nextCell.col);
              const newHighlight = new Set();
              for (const w of newFound) {
                const cellsOfW = getCellsOfWord(w);
                for (const c of cellsOfW) {
                  newHighlight.add(`${c.row},${c.col}`);
                }
              }
              if (newHighlight.size > 0) setHighlightedCells(newHighlight);
            }
          }
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [selectedCell, matrix, findWordsForCell, getCellsOfWord]);

  // --- Отрисовка ---
  const draw = useCallback((ctx, canvasWidth, canvasHeight) => {
    const rows = matrix.length;
    const cols = matrix[0]?.length || 0;

    ctx.clearRect(0, 0, canvasWidth, canvasHeight);
    ctx.save();

    ctx.translate(offsetX, offsetY);
    ctx.scale(scale, scale);

    // Вычисляем видимую область
    const topLeftX = -offsetX / scale;
    const topLeftY = -offsetY / scale;
    const bottomRightX = (canvasWidth - offsetX) / scale;
    const bottomRightY = (canvasHeight - offsetY) / scale;
    const padding = cellSize;
    const startRow = Math.max(0, Math.floor((topLeftY - padding) / cellSize));
    const endRow = Math.min(rows - 1, Math.floor((bottomRightY + padding) / cellSize));
    const startCol = Math.max(0, Math.floor((topLeftX - padding) / cellSize));
    const endCol = Math.min(cols - 1, Math.floor((bottomRightX + padding) / cellSize));

    if (startRow > endRow || startCol > endCol) {
      ctx.restore();
      return;
    }

    // --- Рисуем клетки ---
    for (let r = startRow; r <= endRow; r++) {
      for (let c = startCol; c <= endCol; c++) {
        const x = c * cellSize;
        const y = r * cellSize;

        // Определяем, подсвечена ли клетка
        const isHighlighted = highlightedCells.has(`${r},${c}`);

        // Фон
        if (isHighlighted) {
          ctx.fillStyle = '#fff3b0'; // светло-жёлтый
        } else {
          ctx.fillStyle = matrix[r]?.[c] || userFilled[r]?.[c] ? '#ffffff' : '#222222';
        }
        ctx.fillRect(x, y, cellSize, cellSize);

        // Рамка
        ctx.strokeStyle = '#333333';
        ctx.lineWidth = 1;
        ctx.strokeRect(x, y, cellSize, cellSize);

        // Буква
        const letter = userFilled[r]?.[c] || matrix[r]?.[c];
        if (letter) {
          ctx.fillStyle = '#000000';
          const fontSize = Math.max(10, cellSize * 0.6);
          ctx.font = `bold ${fontSize}px Arial, sans-serif`;
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          ctx.fillText(letter, x + cellSize / 2, y + cellSize / 2);
        }

        // Индикатор выбранной клетки (синяя рамка)
        if (selectedCell && selectedCell.row === r && selectedCell.col === c) {
          ctx.strokeStyle = '#1e90ff';
          ctx.lineWidth = 3;
          ctx.strokeRect(x + 2, y + 2, cellSize - 4, cellSize - 4);
        }
      }
    }

    ctx.restore();
  }, [matrix, userFilled, offsetX, offsetY, scale, cellSize, highlightedCells, selectedCell]);

  // --- Перерисовка при изменении состояний ---
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

  // --- Обработчики для зума/пана (без изменений) ---
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
      onClick={handleCanvasClick}
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
      <div
        style={{position: 'absolute', bottom: 16, left: 16, color: '#555', fontSize: 14, background: '#fff', padding: '4px 10px', borderRadius: 4}}>
        Кликните на клетку → введите букву
      </div>
    </div>
  );
};
