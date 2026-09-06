import {VisibilityOutlined} from '@mui/icons-material';
import {IconButton, Tooltip} from '@mui/material';
import {useSocket} from '@shared/lib/socket/hooks';
import {setSpectator} from '../model/setSpectatorService';

export const BecomeSpectatorButton = ({gameId, phase}) => {
  const {send} = useSocket();

  const handleClick = () => {
    if (phase !== 'PLAYING' || window.confirm('Вы точно уверены что хотите выйти во время игры. Все ваши очки и слова будут утеряны!!!')) {
      setSpectator(send, gameId, true);
    }
  };

  return (
    <Tooltip arrow disableInteractive title="Стать зрителем">
      <IconButton color="primary" onClick={handleClick}>
        <VisibilityOutlined/>
      </IconButton>
    </Tooltip>
  );
};
