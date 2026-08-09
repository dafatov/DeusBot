import {VisibilityOutlined} from '@mui/icons-material';
import {IconButton, Tooltip} from '@mui/material';
import {useSocket} from '@shared/lib/socket/hooks';
import {setSpectator} from '../model/setSpectatorService';

export const BecomeSpectatorButton = ({gameId}) => {
  const {send} = useSocket();
  const handleClick = () => setSpectator(send, gameId, true);

  return (
    <Tooltip arrow disableInteractive title="Стать зрителем">
      <IconButton color="primary" onClick={handleClick}>
        <VisibilityOutlined/>
      </IconButton>
    </Tooltip>
  );
};
