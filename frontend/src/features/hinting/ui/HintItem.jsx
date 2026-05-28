import {setHintGuessed} from '@features/hinting/model/hintService';
import {RadioButtonCheckedOutlined, RadioButtonUncheckedOutlined} from '@mui/icons-material';
import {ListItem, ListItemText, Rating} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {useSocket} from '@shared/lib/socket/hooks';

export const HintItem = ({hint, currentTeam}) => {
  const {gameId, paused, me: {team, isCaptain}} = useGame();
  const {send} = useSocket();

  const handleChangeGuessed = value => {
    setHintGuessed(send, gameId, hint?.team, hint?.word, value);
  };

  return (
    <ListItem>
      <ListItemText primary={hint.word}/>
      <Rating
        readOnly={isCaptain || team !== currentTeam}
        disabled={paused}
        icon={<RadioButtonCheckedOutlined sx={{width: '16px', height: '16px'}}/>}
        emptyIcon={<RadioButtonUncheckedOutlined sx={{width: '16px', height: '16px'}}/>}
        value={hint.guessed}
        onChange={(_, value) => handleChangeGuessed(value)}
        max={hint.count}
        defaultValue={hint.guessed}
        size="small"
      />
    </ListItem>
  );
};
