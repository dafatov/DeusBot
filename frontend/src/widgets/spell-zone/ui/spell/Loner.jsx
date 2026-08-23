import {Man4} from '@mui/icons-material';
import {Typography} from '@mui/material';
import {useMemo} from 'react';
import {SpellButton} from './SpellButton';

export const Loner = ({disabled}) => {
  const value = useMemo(() => ({
    group: 'area',
    type: 'loner',
    affect: 'none',
    radius: 3,
    onCellClick: ({x, y}) => ({
      group: 'area',
      type: 'loner',
      x,
      y
    })
  }), []);

  const tooltip = useMemo(() => {
    return (
      <Typography>
        Вскрывает случайную букву из тех которые не встречаются повторно в радиусе
      </Typography>
    );
  }, []);

  return (
    <SpellButton tooltip={tooltip} value={value} disabled={disabled}>
      <Man4 sx={{width: 48, height: 48}}/>
    </SpellButton>
  );
};
