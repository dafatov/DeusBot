import {forwardRef} from 'react';

export const Canvas = forwardRef(({onClick, onMouseMove, onMouseLeave, onKeyDown, cursor}, ref) => {
  return (
    <canvas
      ref={ref}
      tabIndex={0}
      onClick={onClick}
      onMouseMove={onMouseMove}
      onMouseLeave={onMouseLeave}
      onKeyDown={onKeyDown}
      style={{
        display: 'block',
        width: '100%',
        height: '100%',
        cursor,
        outline: 'none',
      }}
    />
  );
});
