import {FREQUENCY_RADIUS} from '@entities/game/model/crossward/constants';
import {Box, Button, Grid, Typography} from '@mui/material';

const groups = [
  {key: 'FREQUENCY_HIGH', label: 'Высокая частота'},
  {key: 'FREQUENCY_MEDIUM', label: 'Средняя частота'},
  {key: 'FREQUENCY_LOW', label: 'Низкая частота'},
];

export const LetterGridPopover = ({letterTags, selectedLetter, onSelectLetter, onClose}) => {
  const handleSelect = (letter) => {
    onSelectLetter(letter);
    onClose();
  };

  return (
    <Box sx={{maxWidth: 400}}>
      {groups.map((group) => {
        const letters = letterTags?.[group.key] || [];

        if (letters.length === 0) return null;

        return (
          <Box key={group.key} sx={t => ({padding: t.spacing()})}>
            <Typography>{group.label} (радиус {FREQUENCY_RADIUS[group.key]})</Typography>
            <Grid container spacing={1}>
              {letters.map(letter => (
                <Grid item key={letter}>
                  <Button
                    variant={selectedLetter === letter ? 'contained' : 'outlined'}
                    size="small"
                    onClick={() => handleSelect(letter)}
                    sx={{minWidth: 32}}
                  >
                    {letter}
                  </Button>
                </Grid>
              ))}
            </Grid>
          </Box>
        );
      })}
    </Box>
  );
};
