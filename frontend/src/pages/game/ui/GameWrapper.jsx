import {useGame} from '@entities/game/lib/hooks';
import {Box, CircularProgress} from '@mui/material';
import {usePageTitle} from '@shared/lib/page-title/hooks';
import {useSocket} from '@shared/lib/socket/hooks';

export const GameWrapper = ({gameName, children}) => {
  const {connected} = useSocket();
  const {gameId} = useGame();

  usePageTitle(`${gameName} - ${gameId}`);

  if (!connected || !gameId) {
    return (
      <Box sx={{
        height: '100%',
        display: 'flex',
        'justify-content': 'center',
        'align-items': 'center',
      }}>
        <CircularProgress color="secondary" size={100}/>
      </Box>
    );
  }

  return children;
};
