import {PanZoomProvider} from '@entities/cross-ward/lib/PanZoomProvider';
import {CrosswordCanvas} from '@entities/cross-ward/ui/CrosswardCanvas';
import {useGame} from '@entities/game/lib/hooks';
import {Box, Button, CircularProgress} from '@mui/material';
import {useSocket} from '@shared/lib/socket/hooks';
import {useEffect} from 'react';

export const CrossWardContent = () => {
  const {connected, send} = useSocket();
  const {gameId, grid} = useGame();

  useEffect(() => {
    const oldTitle = document.title;

    document.title = `Crossward - ${gameId}`;
    return () => {
      document.title = oldTitle;
    };
  }, [gameId]);

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

  //const stepIcon = (letter) => ({active, completed, className}) => <Avatar className={className}>{letter}</Avatar>;
  return (
    <>
      {/*<Card>
        <Stepper activeStep={2}>
          <Step><StepLabel slots={{stepIcon: stepIcon('A')}}>1</StepLabel></Step>
          <Step><StepLabel slots={{stepIcon: stepIcon('B')}}>2</StepLabel></Step>
          <Step><StepLabel slots={{stepIcon: stepIcon('C')}}>3</StepLabel></Step>
          <Step><StepLabel slots={{stepIcon: stepIcon('D')}}>4</StepLabel></Step>
        </Stepper>
      </Card>*/}
      <PanZoomProvider>
        <CrosswordCanvas matrix={grid}/>
      </PanZoomProvider>
      <Button variant="outlined" onClick={() => send(`/app/game/${gameId}`, JSON.stringify({type: 'cross_ward.start_game'}))}>Создать игру</Button>
    </>
  );
};
