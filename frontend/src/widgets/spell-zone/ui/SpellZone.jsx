import {useGame} from '@entities/game/lib/hooks';
import {Paper, Stack, ToggleButtonGroup} from '@mui/material';
import {useEffect} from 'react';
import {Crosslight} from './spell/Crosslight';
import {Crucifix} from './spell/Crucifix';
import {Echo} from './spell/Echo';
import {Loner} from './spell/Loner';
import {Radar} from './spell/Radar';

export const SpellZone = ({activeSpell, setActiveSpell}) => {
  const {currentEnergy, me: {isCurrentPlayer}, paused} = useGame();

  const isDisabled = currentEnergy < 1 || !isCurrentPlayer || paused;

  useEffect(() => {
    if (isDisabled) {
      setActiveSpell(null);
    }
  }, [isDisabled, setActiveSpell]);

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
          <Loner disabled={isDisabled}/>
          <Radar activeSpell={activeSpell} setActiveSpell={setActiveSpell} disabled={isDisabled}/>
          <Crucifix disabled={isDisabled}/>
          <Echo disabled={isDisabled}/>
          <Crosslight disabled={isDisabled}/>
        </ToggleButtonGroup>
      </Stack>
    </Paper>
  );
};
