import {Typography} from '@mui/material';
import {useMemo} from 'react';
import {SpellButton} from './SpellButton';

export const Crosslight = ({disabled}) => {
  const value = useMemo(() => ({
    group: 'word',
    type: 'crosslight',
    affect: 'none',
    onCellClick: ({wordId}) => ({
      group: 'word',
      type: 'crosslight',
      wordId
    })
  }), []);

  const tooltip = useMemo(() => {
    return (
      <Typography>
        Открытие все пересечений у выбранного слова
      </Typography>
    );
  }, []);

  return (
    <SpellButton tooltip={tooltip} value={value} disabled={disabled}/>
  );
};
