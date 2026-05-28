import {Send} from '@mui/icons-material';
import {IconButton, Stack, TextField} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {useSocket} from '@shared/lib/socket/hooks';
import {useRef, useState} from 'react';
import {addHint} from '../model/hintService.js';
import {isValidCount, isValidWord} from '../model/validators';

export const GuessZone = ({currentTeam}) => {
  const {gameId, paused, filterHints} = useGame();
  const {send} = useSocket();
  const [word, setWord] = useState('');
  const [count, setCount] = useState('');
  const countInputRef = useRef(null);

  const validWord = isValidWord(word) && !filterHints(currentTeam).find(g => g.word === word);
  const validCount = isValidCount(count);

  const handleSendHint = () => {
    if (paused || !validWord || !validCount) {
      return;
    }

    addHint(send, gameId, word, count).then(() => {
      setWord('');
      setCount('');
    });
  };

  const handleWordKeyDown = e => {
    switch (e.key) {
      case ' ':
        e.preventDefault();
        countInputRef.current?.focus();
        break;
      case 'Enter':
        e.preventDefault();
        handleSendHint();
        break;
    }
  };

  const handleCountKeyDown = e => {
    if (e.key !== 'Enter') {
      return;
    }

    e.preventDefault();
    handleSendHint();
  };

  return (
    <Stack direction="row" spacing={1} sx={t => ({padding: t.spacing(1)})}>
      <TextField
        disabled={paused}
        required
        size="small"
        label="Слово"
        error={!validWord && word.length > 0}
        value={word}
        onChange={(e) => setWord(e.target.value)}
        onKeyDown={handleWordKeyDown}
      />
      <TextField
        inputRef={countInputRef}
        disabled={paused}
        required
        size="small"
        label=""
        sx={{maxWidth: '50px'}}
        error={!validCount && count.length > 0}
        value={count}
        onChange={(e) => setCount(e.target.value)}
        onKeyDown={handleCountKeyDown}
      />
      <IconButton
        disabled={!validWord || !validCount || paused}
        variant="outlined"
        onClick={() => handleSendHint()}
      ><Send/></IconButton>
    </Stack>
  );
};
