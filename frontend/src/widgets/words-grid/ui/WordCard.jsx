import {WordTile} from '@entities/game';
import {VoteZone} from '@features/voting';
import {Avatar, Paper} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {COLORS} from '../config/colors';

export const WordCard = ({word, visible}) => {
  const {team, paused, phase, me, filterPlayersByWord} = useGame();

  return (
    <Paper sx={t => {
      const baseColor = visible && word?.color ? COLORS[word?.color] : null;
      return {
        position: 'relative',
        width: '100%',
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'stretch',
        textAlign: 'center',
        border: `solid 1px ${t.palette.primary.main}`,
        opacity: 0.9,
        ...(baseColor && {background: `linear-gradient(0deg, ${baseColor[0]}, ${baseColor[1]})`}),
        ...(visible && word?.revealed && {
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
      {word?.revealed && visible &&
        <Avatar variant="rounded" sx={t => ({
          position: 'absolute',
          top: 0,
          left: 0,
          width: 28,
          height: 28,
          borderRight: 1,
          borderBottom: 1,
          backgroundColor: COLORS[word.revealed.team][0],
          color: t.palette.primary.main,
        })}>{word.revealed.order}</Avatar>
      }
    </Paper>
  );
};
