import {Microsoft, QuestionMark} from '@mui/icons-material';
import {Avatar, Badge, IconButton, Tooltip, Typography} from '@mui/material';
import {useMemo} from 'react';

export const Crosslight = ({selectedWord, handleOnWordSpellClick}) => {
  const onClick = () => selectedWord && handleOnWordSpellClick({type: 'crosslight'});

  const tooltip = useMemo(() => {
    return (
      <Typography>
        Открытие все пересечений у выбранного слова
      </Typography>
    );
  }, []);

  return (
    <Badge
      overlap="circular"
      badgeContent={
        <Tooltip title={tooltip}>
          <QuestionMark/>
        </Tooltip>
      }
    >
      <Avatar sx={{width: 64, height: 64}} variant="square">
        <IconButton disabled={!selectedWord} onClick={onClick}>
          <Microsoft sx={{width: 48, height: 48}}/>
        </IconButton>
      </Avatar>
    </Badge>
  );
};
