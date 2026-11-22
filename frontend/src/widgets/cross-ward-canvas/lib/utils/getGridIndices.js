export const getGridIndices = (canvasRef, event, offsetX, offsetY, scale, cellSize, cols, rows) => {
  const canvas = canvasRef.current;

  if (!canvas) return;

  const rect = canvas.getBoundingClientRect();
  const dpr = window.devicePixelRatio || 1;
  const logicalX = (event.clientX - rect.left) * (canvas.width / rect.width / dpr);
  const logicalY = (event.clientY - rect.top) * (canvas.height / rect.height / dpr);
  const gridX = (logicalX - offsetX) / scale;
  const gridY = (logicalY - offsetY) / scale;
  const x = Math.floor(gridX / cellSize);
  const y = Math.floor(gridY / cellSize);

  if (x < 0 || x >= cols || y < 0 || y >= rows) {
    return null;
  }

  const dx = gridX - (x * cellSize + cellSize / 2);
  const dy = gridY - (y * cellSize + cellSize / 2);

  let sector;
  if (Math.abs(dx) >= Math.abs(dy)) {
    sector = dx >= 0 ? 'E' : 'W';
  } else {
    sector = dy >= 0 ? 'S' : 'N';
  }

  return {x, y, sector};
};
