import {Church, QuestionMark} from '@mui/icons-material';
import {Avatar, Badge, IconButton, Tooltip, Typography} from '@mui/material';
import {useMemo} from 'react';

export const Crucifix = ({setActiveSpell}) => {
  const onClick = () => setActiveSpell(activeSpell => activeSpell?.type === 'crucifix' ? null : {type: 'crucifix', radius: 0});

  const tooltip = useMemo(() => {
    return (
      <Typography>
        Заклинание, раскрывающее клетки в форме креста до тех пор, пока на каждом расстоянии от центра открыты все 4 клетки.
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
          <Church sx={{width: 48, height: 48}}/>
        </IconButton>
      </Avatar>
    </Badge>
  );
};
