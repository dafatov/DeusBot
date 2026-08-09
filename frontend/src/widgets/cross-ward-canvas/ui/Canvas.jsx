import {forwardRef} from 'react';

export const Canvas = forwardRef(({onClick, onMouseMove, onMouseLeave, cursor}, ref) => {
  return (
    <canvas
      ref={ref}
      onClick={onClick}
      onMouseMove={onMouseMove}
      onMouseLeave={onMouseLeave}
      style={{
        display: 'block',
        width: '100%',
        height: '100%',
        cursor,
      }}
    />
  );
});
