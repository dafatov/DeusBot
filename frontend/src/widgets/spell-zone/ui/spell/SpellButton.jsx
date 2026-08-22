import {QuestionMark} from '@mui/icons-material';
import {Badge, ToggleButton, Tooltip} from '@mui/material';

export const SpellButton = ({children, value, tooltip}) => {
  return (
    <ToggleButton color="primary" value={value} variant="outlined" sx={{minWidth: 0}}>
      <Badge badgeContent={
        <Tooltip title={tooltip} disableInteractive>
          <QuestionMark sx={{width: 16, height: 16}}/>
        </Tooltip>
      }>
        {children}
      </Badge>
    </ToggleButton>
  );
};
