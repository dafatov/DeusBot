import {BecomeSpectatorButton} from '@features/change-team';
import {AvatarGroup, Paper, Stack, Tooltip} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {CopyText} from '@shared/ui/CopyText';
import {DiscordAvatar} from '@shared/ui/DiscordAvatar';

export const SpectatorPlayers = () => {
  const {gameId, locked, spectators, me: {isSpectator}} = useGame();

  return (
    <Paper square sx={theme => ({padding: theme.spacing(1), minHeight: '44px'})}>
      <Stack direction="row" sx={{justifyContent: 'space-between', alignItems: 'center'}}>
        <CopyText text={gameId}/>
        <AvatarGroup max={Infinity}>
          {spectators.map(p => (
            <Tooltip arrow disableInteractive title={p.name}>
              <DiscordAvatar id={p.id} name={p.name} avatar={p.avatar} disconnected={p.disconnected}/>
            </Tooltip>
          ))}
          {locked || isSpectator
            ? <></>
            : <BecomeSpectatorButton gameId={gameId}/>}
        </AvatarGroup>
      </Stack>
    </Paper>
  );
};
