import {AbcOutlined} from '@mui/icons-material';
import {Typography} from '@mui/material';
import {useMemo} from 'react';
import {SpellButton} from './SpellButton';

export const Echo = () => {
  const value = useMemo(() => ({
    group: 'word',
    type: 'echo',
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
    <SpellButton tooltip={tooltip} value={value}>
      <AbcOutlined sx={{width: 48, height: 48}}/>
    </SpellButton>
  );
};
