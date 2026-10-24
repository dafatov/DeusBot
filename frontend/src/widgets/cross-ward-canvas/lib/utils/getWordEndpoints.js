export const getWordEndpoints = cells => {
  if (!cells?.length) return null;

  const {minX, maxX, minY, maxY} = cells.reduce((acc, {x, y}) => ({
    minX: Math.min(acc.minX, x),
    maxX: Math.max(acc.maxX, x),
    minY: Math.min(acc.minY, y),
    maxY: Math.max(acc.maxY, y),
  }), {minX: Infinity, maxX: -Infinity, minY: Infinity, maxY: -Infinity});

  if (minX === maxX) {
    const first = cells.find(c => c.y === minY);
    const last = cells.find(c => c.y === maxY);

    return {first, last, size: {x: 1, y: maxY - minY + 1}};
  }

  if (minY === maxY) {
    const first = cells.find(c => c.x === minX);
    const last = cells.find(c => c.x === maxX);

    return {first, last, size: {x: maxX - minX + 1, y: 1}};
  }

  return null;
};
