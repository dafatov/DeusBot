import {useContext} from 'react';
import {PanZoomContext} from './PanZoomContext';

export const usePanZoom = () => {
  const context = useContext(PanZoomContext);
  if (!context) {
    throw new Error('usePanZoom must be used within PanZoomProvider');
  }
  return context;
};
