import {WordTile} from '@entities/game';
import {VoteZone} from '@features/voting';
import {darken, Grid, Paper} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';

const COLORS = {
  RED: ['#c33b23', darken('#c33b23', 0.5)],
  BLUE: ['#2c5f8a', darken('#2c5f8a', 0.5)],
  WHITE: ['#ffffff', darken('#ffffff', 0.5)],
  BLACK: ['#000000', darken('#000000', 0.5)],
};

export const WordsGrid = () => {
  const {words, team, paused, phase, me, filterPlayersByWord} = useGame();

  return (
    <Grid
      container
      spacing={2}
      columns={Math.sqrt(words.length)}
      sx={{justifyContent: 'center', alignItems: 'center', maxWidth: '50vw'}}
    >
      {words.map(word => (
        <Grid
          item
          size={1}
          sx={{aspectRatio: '3/2'}}
        >
          <Paper sx={t => {
            const baseColor = word?.color ? COLORS[word?.color] : null;
            return {
              width: '100%',
              height: '100%',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'stretch',
              textAlign: 'center',
              border: `solid 1px ${t.palette.primary.main}`,
              opacity: 0.9,
              ...(baseColor && {background: `linear-gradient(0deg, ${baseColor[0]}, ${baseColor[1]})`}),
              ...(word?.revealed && {
                opacity: 0.4,
                transform: 'rotateY(360deg)',
              }),
              transition: 'transform 0.6s ease, background 0.6s ease, border 0.6s ease',
              perspective: '600px',
              transformStyle: 'preserve-3d',
            };
          }}>
            <VoteZone
              playerList={filterPlayersByWord(word)}
              vote={{type: 'word', word: word.text}}
              clickable={!me.isCaptain && me.team === team && phase === 'GUESSING' && !paused && !word?.revealed}
            >
              <WordTile key={word} word={word}/>
            </VoteZone>
          </Paper>
        </Grid>
      ))}
    </Grid>
  );
};
