import {useGame} from '@entities/game/lib/hooks';
import {LocationSearching, QuestionMark} from '@mui/icons-material';
import {Avatar, Badge, IconButton, ListSubheader, MenuItem, Select, Tooltip, Typography} from '@mui/material';
import {useMemo, useState} from 'react';

export const FREQUENCY_RADIUS = {
  FREQUENCY_HIGH: 1,
  FREQUENCY_MEDIUM: 2,
  FREQUENCY_LOW: 3,
};

export const Radar = ({setActiveSpell}) => {
  const {letterTags} = useGame();
  const [radarSkillLetter, setRadarSkillLetter] = useState({});

  const onClick = () => setActiveSpell(activeSpell => activeSpell?.type === 'radar' ? null : {type: 'radar', ...radarSkillLetter});

  const tooltip = useMemo(() => {
    return (
      <Typography>
        Вскрывает один из выбранных символов во всех клетках в радиусе со следующей закономерностью:
        {Object.entries(FREQUENCY_RADIUS).map(([key, r]) => (
          <p key={key}>Радиус {r} [{2 * r + 1}x{2 * r + 1}] - {letterTags?.[key]?.sort()?.join(', ') ?? '?'}</p>
        ))}
      </Typography>
    );
  }, [letterTags]);

  return (
    <Badge
      overlap="circular"
      badgeContent={
        <Tooltip title={tooltip}>
          <QuestionMark/>
        </Tooltip>
      }
    >
      <Badge
        overlap="circular"
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        badgeContent={
          <Select
            variant="outlined"
            value={radarSkillLetter ?? ''}
            onChange={(e) => {
              setRadarSkillLetter(e.target.value);
            }}
            renderValue={(p) => <>{p.letter}</>}
          >
            {Object.entries(FREQUENCY_RADIUS).flatMap(([key, radius]) => letterTags?.[key] ? [
              <ListSubheader key={`${key}-header`}>{2 * radius + 1}x{2 * radius + 1}</ListSubheader>,
              ...letterTags[key].sort().map((l) => (
                <MenuItem key={l} value={{radius, letter: l}}>
                  {l}
                </MenuItem>
              )),
            ] : [])}
          </Select>
        }
      >
        <Avatar sx={{width: 64, height: 64}} variant="square">
          <IconButton disabled={!radarSkillLetter} onClick={onClick}>
            <LocationSearching sx={{width: 48, height: 48}}/>
          </IconButton>
        </Avatar>
      </Badge>
    </Badge>
  );
};
