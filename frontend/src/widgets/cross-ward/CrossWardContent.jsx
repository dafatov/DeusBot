import {PanZoomProvider} from '@entities/cross-ward/lib/PanZoomProvider';
import {CrosswordCanvas} from '@entities/cross-ward/ui/CrosswardCanvas';
import {useGame} from '@entities/game/lib/hooks';
import {Box, CircularProgress} from '@mui/material';
import {useSocket} from '@shared/lib/socket/hooks';
import {useEffect} from 'react';

export const CrossWardContent = () => {
  const {connected} = useSocket();
  const {gameId, grid} = useGame();

  useEffect(() => {
    const oldTitle = document.title;

    document.title = `Crossward - ${gameId}`;
    return () => {
      document.title = oldTitle;
    };
  }, [gameId]);

  if (!connected || !gameId) {
    return (
      <Box sx={{
        height: '100%',
        display: 'flex',
        'justify-content': 'center',
        'align-items': 'center',
      }}>
        <CircularProgress color="secondary" size={100}/>
      </Box>
    );
  }

  return (
    <PanZoomProvider>
      <CrosswordCanvas matrix={grid}/>
    </PanZoomProvider>
  );
};
