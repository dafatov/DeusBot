import {useGame} from '@entities/game/lib/hooks';
import {Box, CircularProgress} from '@mui/material';
import {useSocket} from '@shared/lib/socket/hooks';
import {useEffect} from 'react';
import {CrosswordCanvas3} from '../../../trash/CrosswordCanvas3';

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

  const matrix = [
    ['К', 'О', 'Т', null, 'М', 'О', 'Р', 'Е'],
    ['А', null, 'А', null, 'О', null, null, null],
    ['Р', 'А', 'К', null, 'Р', 'Е', 'К', 'А'],
    [null, null, null, null, 'О', null, null, null],
    ['Л', 'Е', 'С', null, 'Н', 'О', 'С', ''],
    ['О', null, 'Т', null, 'Ы', null, null, null],
    ['С', 'Л', 'О', 'Н', 'К', 'А', null, null],
  ];

  return (
    // <CrosswordCanvas matrix={grid.reduce((acc, f) => {const rr = acc[f.y]; if (!rr) {acc[f.y] = []} acc[f.y][f.x] = f.letter; return acc}, [])}/>
    <CrosswordCanvas3 matrix={grid}/>
  );
};
