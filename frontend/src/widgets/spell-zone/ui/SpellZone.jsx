import {useGame} from '@entities/game/lib/hooks';
import {Paper, Stack} from '@mui/material';
import {useSocket} from '@shared/lib/socket/hooks';
import {useSpell} from '../../cross-ward/model/submitWordService';
import {Echo} from './Echo';
import {Radar} from './Radar';

export const SpellZone = ({setActiveSpell, selectedWord}) => {
  const {send} = useSocket();
  const {gameId} = useGame();

  const handleOnWordSpellClick = ({type}) => {
    useSpell(send, gameId, {type, wordId: selectedWord});
  };

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
        <Echo selectedWord={selectedWord} handleOnWordSpellClick={handleOnWordSpellClick}/>
      </Stack>
    </Paper>
  );
};
