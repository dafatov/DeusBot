import {Background} from '@entities/game';
import {useGame} from '@entities/game/lib/hooks';
import {CodeNamesControl} from '@features/game-control';
import {Stack} from '@mui/material';
import {CodeNamesSpectatorPlayers} from '@widgets/spectators';
import {TeamZone} from '@widgets/team-zone';
import {WordsGrid} from '@widgets/words-grid';

export const CodeNamesContent = () => {
  const {me: {isHost}} = useGame();

  return (
    <Background>
      <Stack container direction="column" spacing={2} sx={{height: '100vh'}}>
        <CodeNamesSpectatorPlayers/>
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
          ? <CodeNamesControl/>
          : <></>}
      </Stack>
    </Background>
  );
};
