import {Background} from '@entities/game';
import {GameControl} from '@features/game-control';
import {Box, CircularProgress, Stack} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {useSocket} from '@shared/lib/socket/hooks';
import {SpectatorPlayers} from '@widgets/spectators';
import {TeamZone} from '@widgets/team-zone';
import {WordsGrid} from '@widgets/words-grid';
import {useEffect} from 'react';

export const CodeNamesContent = () => {
  const {connected} = useSocket();
  const {gameId, me: {isHost}} = useGame();

  useEffect(() => {
    const oldTitle = document.title;

    document.title = `Codenames - ${gameId}`;
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

  return (
    <Background>
      <Stack container direction="column" spacing={2} sx={{height: '100vh'}}>
        <SpectatorPlayers/>
        <Stack
          direction="row"
          spacing={2}
          sx={{justifyContent: 'space-between', alignItems: 'center', height: '100vh'}}
        >
          <TeamZone currentTeam="RED"/>
          <WordsGrid/>
          <TeamZone currentTeam="BLUE"/>
        </Stack>
        {isHost
          ? <GameControl/>
          : <></>}
      </Stack>
    </Background>
  );
};
