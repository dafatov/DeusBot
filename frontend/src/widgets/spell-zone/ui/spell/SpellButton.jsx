import {SPELL_ICON} from '@entities/game/model/crossward/constants';
import {QuestionMark} from '@mui/icons-material';
import {Badge, Box, ToggleButton, Tooltip} from '@mui/material';

const AFFECT_COLOR = {
  none: '#50ef50',
  'end_turn': '#ff0000',
};

export const SpellButton = ({children, value, disabled, tooltip}) => {
  const Icon = value?.type ? SPELL_ICON[value.type] : null;

  return (
    <ToggleButton disabled={disabled} color="primary" value={value} variant="outlined" sx={{minWidth: 0}}>
      <Badge
        badgeContent={
          <Tooltip title={tooltip} disableInteractive>
            <QuestionMark sx={{width: 16, height: 16}}/>
          </Tooltip>
        }
      >
        <Box
          component="span"
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            filter: `drop-shadow(0 0 10px color-mix(in srgb, ${AFFECT_COLOR[value.affect]} ${disabled ? 0 : 75}%, transparent))`,
          }}
        >
          {children || (Icon && <Icon sx={{width: 48, height: 48}}/>)}
        </Box>
      </Badge>
    </ToggleButton>
  );
};
