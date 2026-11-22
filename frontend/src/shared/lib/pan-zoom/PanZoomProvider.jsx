import React, {useCallback, useEffect, useRef, useState} from 'react';
import {PanZoomContext} from './PanZoomContext';

const MIN_SCALE = 0.2;
const MAX_SCALE = 5;
const ZOOM_STEP = 1.2;

export const PanZoomProvider = ({children, initialScale = 1, initialOffsetX = 0, initialOffsetY = 0}) => {
  const containerRef = useRef(null);

  const [scale, setScale] = useState(initialScale);
  const [offsetX, setOffsetX] = useState(initialOffsetX);
  const [offsetY, setOffsetY] = useState(initialOffsetY);

  const [isDragging, setIsDragging] = useState(false);
  const [dragStart, setDragStart] = useState({x: 0, y: 0});

  const handleMouseDown = useCallback((e) => {
    setIsDragging(true);
    setDragStart({x: e.clientX - offsetX, y: e.clientY - offsetY});
  }, [offsetX, offsetY]);

  const handleMouseMove = useCallback((e) => {
    if (!isDragging) return;
    setOffsetX(e.clientX - dragStart.x);
    setOffsetY(e.clientY - dragStart.y);
  }, [isDragging, dragStart]);

  const handleMouseUp = useCallback(() => {
    setIsDragging(false);
  }, []);

  const handleWheel = useCallback((e) => {
    e.preventDefault();

    const container = containerRef.current;

    if (!container) {
      return;
    }

    const rect = container.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;

    const worldX = (mouseX - offsetX) / scale;
    const worldY = (mouseY - offsetY) / scale;

    const delta = e.deltaY > 0 ? 1 / ZOOM_STEP : ZOOM_STEP;
    const newScale = Math.min(Math.max(scale * delta, MIN_SCALE), MAX_SCALE);

    setScale(newScale);
    setOffsetX(mouseX - worldX * newScale);
    setOffsetY(mouseY - worldY * newScale);
  }, [offsetX, offsetY, scale]);

  const resetView = useCallback((gridWidth, gridHeight) => {
    const container = containerRef.current;

    if (!container) {
      return;
    }

    const rect = container.getBoundingClientRect();

    setScale(1);
    setOffsetX((rect.width - gridWidth) / 2);
    setOffsetY((rect.height - gridHeight) / 2);
  }, []);

  const moveToView = useCallback((worldX, worldY) => {
    const container = containerRef.current;

    if (!container) {
      return;
    }

    const rect = container.getBoundingClientRect();

    setOffsetX(rect.width / 2 - worldX * scale);
    setOffsetY(rect.height / 2 - worldY * scale);
  }, [scale]);

  useEffect(() => {
    const container = containerRef.current;

    if (!container) {
      return;
    }

    container.addEventListener('wheel', handleWheel, {passive: false});

    return () => container.removeEventListener('wheel', handleWheel);
  }, [handleWheel]);

  return (
    <PanZoomContext.Provider value={{containerRef, scale, offsetX, offsetY, resetView, moveToView, isDragging}}>
      <div
        ref={containerRef}
        style={{
          width: '100%',
          height: '100%',
          overflow: 'hidden',
          position: 'relative',
          cursor: isDragging ? 'grabbing' : 'grab',
          userSelect: 'none',
        }}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
      >
        {children}
      </div>
    </PanZoomContext.Provider>
  );
};
