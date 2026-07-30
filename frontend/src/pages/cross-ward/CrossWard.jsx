import {Box, CircularProgress} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {useSocket} from '@shared/lib/socket/hooks';
import {useEffect} from 'react';
import {CrosswordCanvas2} from './trash/CrosswordCanvas2';

export const CrossWardContent = () => {
  const {connected} = useSocket();
  const {gameId} = useGame();

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

  const words = [
    {word: 'КОТ', row: 0, col: 0, direction: 'across'},
    {word: 'МОРЕ', row: 0, col: 4, direction: 'across'},
    {word: 'КАР', row: 1, col: 0, direction: 'down'},
    {word: 'РАК', row: 2, col: 0, direction: 'across'},
    {word: 'РЕКА', row: 2, col: 4, direction: 'across'},
    {word: 'ЛЕС', row: 4, col: 0, direction: 'across'},
    {word: 'НОС', row: 4, col: 4, direction: 'across'},
    {word: 'СЛОН', row: 4, col: 0, direction: 'down'},
    {word: 'ОТ', row: 5, col: 0, direction: 'across'},
    {word: 'СЛОН', row: 6, col: 0, direction: 'across'}, // обратите внимание на пересечения
  ];


  return (
    <CrosswordCanvas2 matrix={matrix} words={words}/>
  );
};
