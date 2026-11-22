export const normalizeGrid = grid => {
  if (!Array.isArray(grid) || grid.length === 0) {
    return {cells: new Map(), size: {x: 0, y: 0}, shift: {x: 0, y: 0}};
  }

  const {minX, maxX, minY, maxY} = grid.reduce((acc, {x, y}) => ({
    minX: Math.min(acc.minX, x),
    maxX: Math.max(acc.maxX, x),
    minY: Math.min(acc.minY, y),
    maxY: Math.max(acc.maxY, y),
  }), {minX: Infinity, maxX: -Infinity, minY: Infinity, maxY: -Infinity});

  return {
    cells: new Map(grid.map(({x, y, ...props}) => [`${x - minX},${y - minY}`, props])),
    size: {x: maxY - minY + 1, y: maxX - minX + 1},
    shift: {x: minX, y: minY},
  };
};
