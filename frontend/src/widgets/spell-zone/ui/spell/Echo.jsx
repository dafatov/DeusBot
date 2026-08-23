import {AbcOutlined} from '@mui/icons-material';
import {Typography} from '@mui/material';
import {useMemo} from 'react';
import {SpellButton} from './SpellButton';

export const Echo = ({disabled}) => {
  const value = useMemo(() => ({
    group: 'word',
    type: 'echo',
    affect: 'none',
    onCellClick: ({wordId}) => ({
      group: 'word',
      type: 'echo',
      wordId
    })
  }), []);

  const tooltip = useMemo(() => {
    return (
      <Typography>
        Вскрывает является ли согласной или гласной каждую букву выбранного слова
      </Typography>
    );
  }, []);

  return (
    <SpellButton tooltip={tooltip} value={value} disabled={disabled}>
      <AbcOutlined sx={{width: 48, height: 48}}/>
    </SpellButton>
  );
};
