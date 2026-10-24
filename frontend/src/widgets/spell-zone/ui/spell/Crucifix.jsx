import {Typography} from '@mui/material';
import {useMemo} from 'react';
import {SpellButton} from './SpellButton';

export const Crucifix = ({disabled}) => {
  const value = useMemo(() => ({
    group: 'area',
    type: 'crucifix',
    affect: 'end_turn',
    radius: 0,
    onCellClick: ({x, y}) => ({
      group: 'area',
      type: 'crucifix',
      x,
      y
    })
  }), []);

  const tooltip = useMemo(() => {
    return (
      <Typography>
        Заклинание, раскрывающее клетки в форме креста до тех пор, пока на каждом расстоянии от центра открыты все 4 клетки.
      </Typography>
    );
  }, []);

  return (
    <SpellButton tooltip={tooltip} value={value} disabled={disabled}/>
  );
};
