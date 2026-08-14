import {Paper, Stack} from '@mui/material';
import {Echo} from './Echo';
import {Radar} from './Radar';

export const SpellZone = ({setActiveSpell}) => {
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
        <Radar setActiveSpell={setActiveSpell}/>
        <Echo/>
      </Stack>
    </Paper>
  );
};
