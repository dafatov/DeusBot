import {Paper, Stack, ToggleButtonGroup} from '@mui/material';
import {Crosslight} from './spell/Crosslight';
import {Crucifix} from './spell/Crucifix';
import {Echo} from './spell/Echo';
import {Loner} from './spell/Loner';
import {Radar} from './spell/Radar';

export const SpellZone = ({activeSpell, setActiveSpell}) => {
  return (
    <Paper
      sx={(t) => ({
        position: 'absolute',
        left: '50%',
        transform: 'translateX(-50%)',
        top: 76,
        zIndex: 2,
        opacity: 0.8,
        padding: t.spacing(),
      })}
    >
      <Stack direction="row" spacing={1}>
        <ToggleButtonGroup exclusive value={activeSpell} onChange={(_, newValue) => setActiveSpell(newValue)}>
          <Loner/>
          <Radar activeSpell={activeSpell} setActiveSpell={setActiveSpell}/>
          <Crucifix/>
          <Echo/>
          <Crosslight/>
        </ToggleButtonGroup>
      </Stack>
    </Paper>
  );
};
