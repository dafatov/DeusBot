import {useCallback, useState} from 'react';

export const useCellHover = () => {
  const [hoveredCell, setHoveredCell] = useState(null);

  const onHover = useCallback(data => {
    setHoveredCell(data);
  }, [setHoveredCell]);

  return {hoveredCell, onHover};
};
