import {Microsoft} from '@mui/icons-material';
import {Typography} from '@mui/material';
import {useMemo} from 'react';
import {SpellButton} from './SpellButton';

export const Crosslight = () => {
  const value = useMemo(() => ({
    group: 'word',
    type: 'crosslight',
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
    <SpellButton tooltip={tooltip} value={value}>
      <Microsoft sx={{width: 48, height: 48}}/>
    </SpellButton>
  );
};
