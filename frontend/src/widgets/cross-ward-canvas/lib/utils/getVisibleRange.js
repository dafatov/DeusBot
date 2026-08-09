export const getVisibleRange = ({x: offsetX, y: offsetY}, scale, width, height, cellSize, {x: rows, y: cols}) => {
  const topLeftX = -offsetX / scale;
  const topLeftY = -offsetY / scale;
  const bottomRightX = (width - offsetX) / scale;
  const bottomRightY = (height - offsetY) / scale;

  const visibleMinX = topLeftX - cellSize;
  const visibleMinY = topLeftY - cellSize;
  const visibleMaxX = bottomRightX + cellSize;
  const visibleMaxY = bottomRightY + cellSize;

  const startRow = Math.max(0, Math.floor(visibleMinY / cellSize));
  const endRow = Math.min(rows - 1, Math.floor(visibleMaxY / cellSize));
  const startCol = Math.max(0, Math.floor(visibleMinX / cellSize));
  const endCol = Math.min(cols - 1, Math.floor(visibleMaxX / cellSize));

  return {startRow, endRow, startCol, endCol};
};
