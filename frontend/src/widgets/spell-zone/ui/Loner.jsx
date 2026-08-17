import {Man4, QuestionMark} from '@mui/icons-material';
import {Avatar, Badge, IconButton, Tooltip, Typography} from '@mui/material';
import {useMemo} from 'react';

export const Loner = ({setActiveSpell}) => {
  const onClick = () => setActiveSpell(activeSpell => activeSpell?.type === 'loner' ? null : {type: 'loner', radius: 3});

  const tooltip = useMemo(() => {
    return (
      <Typography>
        Вскрывает случайную букву из тех которые не встречаются повторно в радиусе
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
        <IconButton onClick={onClick}>
          <Man4 sx={{width: 48, height: 48}}/>
        </IconButton>
      </Avatar>
    </Badge>
  );
};
