import {useGame} from '@entities/game/lib/hooks';
import {AdsClick, LocationSearching} from '@mui/icons-material';
import {Badge, Box, IconButton, Popover, Typography} from '@mui/material';
import {useMemo, useState} from 'react';
import {findDefaultLetterAndRadius} from '../../lib/findDefaultLetter';
import {getRadiusForLetter} from '../../lib/getRadiusForLetter';
import {FREQUENCY_RADIUS} from '../../model/constants';
import {LetterGridPopover} from './radar/LetterGridPopover';
import {SpellButton} from './SpellButton';

const createSpell = (letter, radius) => ({
  group: 'area',
  type: 'radar',
  radius,
  letter,
  onCellClick: ({x, y}) => ({
    group: 'area',
    type: 'radar',
    x,
    y,
    letter,
  }),
});

export const Radar = ({activeSpell, setActiveSpell}) => {
  const {letterTags} = useGame();

  const [defaultLetter, defaultRadius] = useMemo(() => findDefaultLetterAndRadius(letterTags), [letterTags]);

  const [spell, setSpell] = useState(() => activeSpell?.type === 'radar' ? activeSpell : createSpell(defaultLetter, defaultRadius));

  const tooltip = useMemo(() => (
    <Typography>
      Вскрывает один из выбранных символов во всех клетках в радиусе со следующей закономерностью:
      {Object.entries(FREQUENCY_RADIUS).map(([key, r]) => (
        <p key={key}>
          Радиус {r} [{2 * r + 1}x{2 * r + 1}] -{letterTags?.[key]?.sort()?.join(', ') ?? '?'}
        </p>
      ))}
    </Typography>
  ), [letterTags]);

  const [anchorEl, setAnchorEl] = useState();

  const handleLetterSelect = letter => {
    const newRadius = getRadiusForLetter(letter, letterTags);
    const newSpell = createSpell(letter, newRadius);
    setSpell(newSpell);

    if (activeSpell?.type === 'radar') {
      setActiveSpell?.(newSpell);
    }

    setAnchorEl(null);
  };

  return (
    <SpellButton tooltip={tooltip} value={spell}>
      <Badge
        anchorOrigin={{vertical: 'bottom', horizontal: 'right'}}
        onMouseDown={(e) => e.stopPropagation()}
        badgeContent={
          <IconButton
            onClick={(e) => {
              setAnchorEl(e.currentTarget);
              e.stopPropagation();
            }}
          >
            <AdsClick sx={{width: 16, height: 16}}/>
          </IconButton>
        }
      >
        <Box sx={{position: 'relative', display: 'inline-flex'}}>
          <LocationSearching sx={{width: 48, height: 48}}/>
          <Typography
            color="inherit"
            sx={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              userSelect: 'none',
              pointerEvents: 'none',
            }}
          >
            {spell.letter ?? ''}
          </Typography>
        </Box>
        <Popover
          open={Boolean(anchorEl)}
          anchorEl={anchorEl}
          onClick={(e) => e.stopPropagation()}
          onClose={() => setAnchorEl(null)}
          onMouseDown={(e) => e.stopPropagation()}
          anchorOrigin={{vertical: 'bottom', horizontal: 'right'}}
        >
          <LetterGridPopover
            letterTags={letterTags}
            selectedLetter={spell.letter}
            onSelectLetter={handleLetterSelect}
            onClose={() => setAnchorEl(null)}
          />
        </Popover>
      </Badge>
    </SpellButton>
  );
};
