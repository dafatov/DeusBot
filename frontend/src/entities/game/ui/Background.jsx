import {Box} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';

export const Background = ({children}) => {
  const {score, team, phase} = useGame();

  let redMask = 'linear-gradient(90deg, black 0%, black 25%, transparent 75%, transparent 100%)';
  let blueMask = 'linear-gradient(90deg, transparent 0%, transparent 25%, black 75%, black 100%)';

  if (phase === 'FINISHED') {
    switch (team) {
      case 'RED':
        redMask = 'linear-gradient(90deg, black 80%, transparent 100%)';
        blueMask = 'linear-gradient(90deg, transparent 80%, black 100%)';
        break;
      case 'BLUE':
        redMask = 'linear-gradient(90deg, black 0%, transparent 20%)';
        blueMask = 'linear-gradient(90deg, transparent 0%, black 20%)';
        break;
    }
  }

  return (
    <Box sx={{position: 'relative', minHeight: '100vh', width: '100%', overflow: 'hidden'}}>
      <Box sx={{
        position: 'absolute',
        top: 0, left: 0, width: '100%', height: '100%',
        backgroundColor: '#c23b22',
        maskImage: redMask,
        WebkitMaskImage: redMask,
        zIndex: 0,
      }}/>
      <Box sx={{
        position: 'absolute',
        top: 0, left: 0, width: '100%', height: '100%',
        backgroundColor: '#2c5f8a',
        maskImage: blueMask,
        WebkitMaskImage: blueMask,
        zIndex: 0,
      }}/>
      <Box sx={{
        position: 'absolute',
        top: 0, left: 0, width: '50%', height: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-start',
        paddingLeft: 0,
        fontSize: '100vh',
        fontWeight: 'bold',
        color: 'rgba(80, 20, 5, 0.45)',
        pointerEvents: 'none',
        zIndex: 0.5,
        userSelect: 'none',
        fontFamily: 'Arial, sans-serif',
      }}>
        {score?.RED}
      </Box>
      <Box sx={{
        position: 'absolute',
        top: 0, right: 0, width: '50%', height: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        paddingRight: 0,
        fontSize: '100vh',
        fontWeight: 'bold',
        color: 'rgba(15, 35, 55, 0.45)',
        pointerEvents: 'none',
        zIndex: 0.5,
        userSelect: 'none',
        fontFamily: 'Arial, sans-serif',
      }}>
        {score?.BLUE}
      </Box>
      <Box sx={{
        position: 'relative',
        zIndex: 1,
        width: '100%',
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column'
      }}>
        {children}
      </Box>
    </Box>
  );
};
