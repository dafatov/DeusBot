import {useCallback} from 'react';
import {getGridIndices} from '../utils/getGridIndices';

export const useCrosswordMouseEvents = (onCellClick, onCellHover, canvasRef, offset, scale, cellSize, size, cells) => {
  const onEvent = useCallback((e, callback) => {
    if (!callback) return;

    const indices = getGridIndices(canvasRef, e, offset.x, offset.y, scale, cellSize, size.y, size.x);

    if (indices) {
      const {x, y} = indices;

      callback({x, y, cell: cells.get(`${x},${y}`)});
    } else {
      callback(null);
    }
  }, [canvasRef, offset, scale, cellSize, size, cells]);

  const onClick = useCallback(e => {
    onEvent(e, onCellClick);
  }, [onEvent, onCellClick]);

  const onMouseMove = useCallback(e => {
    onEvent(e, onCellHover);
  }, [onEvent, onCellHover]);

  const onMouseLeave = useCallback(e => {
    onEvent(e, onCellHover);
  }, [onEvent, onCellHover]);

  return {onClick, onMouseMove, onMouseLeave};
};
