import {VisibilityOutlined} from '@mui/icons-material';
import {IconButton, Tooltip} from '@mui/material';
import {useSocket} from '@shared/lib/socket/hooks';
import {changeTeam} from '../model/changeTeamService.js';

export const BecomeSpectatorButton = ({gameId}) => {
  const {send} = useSocket();
  const handleClick = () => changeTeam(send, gameId, 'SPECTATOR');

  return (
    <Tooltip arrow disableInteractive title="Стать зрителем">
      <IconButton color="primary" onClick={handleClick}>
        <VisibilityOutlined/>
      </IconButton>
    </Tooltip>
  );
};
