import {AbcOutlined, QuestionMark} from '@mui/icons-material';
import {Avatar, Badge, IconButton, Tooltip, Typography} from '@mui/material';
import {useMemo} from 'react';

export const Echo = ({selectedWord, handleOnWordSpellClick}) => {
  const onClick = () => selectedWord && handleOnWordSpellClick({type: 'echo'});

  const tooltip = useMemo(() => {
    return (
      <Typography>
        Вскрывает является ли согласной или гласной каждую букву выбранного слова
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
          <AbcOutlined sx={{width: 48, height: 48}}/>
        </IconButton>
      </Avatar>
    </Badge>
  );
};
