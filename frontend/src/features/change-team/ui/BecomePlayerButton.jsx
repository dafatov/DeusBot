import {Button} from '@mui/material';
import {useSocket} from '@shared/lib/socket/hooks';
import {changeTeam} from '../model/changeTeamService.js';

export const BecomePlayerButton = ({gameId, team}) => {
  const {send} = useSocket();
  const handleClick = () => changeTeam(send, gameId, team, false);

  return (
    <Button onClick={handleClick} sx={t => ({width: '100%', minHeight: '40px', padding: t.spacing(2)})}>
      Стать игроком
    </Button>
  );
};
