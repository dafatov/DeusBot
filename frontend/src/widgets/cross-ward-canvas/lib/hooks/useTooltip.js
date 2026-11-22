import {useCallback, useEffect, useRef, useState} from 'react';

const SHOW_DELAY_FIRST = 300;

export const useTooltip = (hoveredCell, activeSpell, isDragging) => {
  const [data, setData] = useState(null);
  const [isHoveringTooltip, setIsHoveringTooltip] = useState(false);
  const showTimer = useRef(null);

  const clearShowTimer = useCallback(() => clearTimeout(showTimer.current), []);

  const onTooltipEnter = useCallback(() => setIsHoveringTooltip(true), []);

  const onTooltipLeave = useCallback(() => setIsHoveringTooltip(false), []);

  useEffect(() => {
    if (activeSpell || isDragging) {
      clearShowTimer();
      setData(null);
      return;
    }

    if (isHoveringTooltip && data) {
      clearShowTimer();
      return;
    }

    if (!hoveredCell?.cell) {
      clearShowTimer();
      setData(null);
      return;
    }

    setIsHoveringTooltip(false);

    if (data) {
      setData(hoveredCell);
      return;
    }

    clearShowTimer();
    showTimer.current = setTimeout(() => {
      setData(hoveredCell);
    }, SHOW_DELAY_FIRST);

    return clearShowTimer;
  }, [hoveredCell, activeSpell, isDragging, isHoveringTooltip, data, clearShowTimer]);

  return {tooltipData: data, onTooltipEnter, onTooltipLeave};
};
