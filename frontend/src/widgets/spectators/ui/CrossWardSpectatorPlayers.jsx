import {useGame} from '@entities/game/lib/hooks';
import {BecomeSpectatorButton} from '@features/set-spectator';
import {AvatarGroup, Paper, Stack, Tooltip} from '@mui/material';
import {CopyText} from '@shared/ui/CopyText';
import {DiscordAvatar} from '@shared/ui/DiscordAvatar';

export const CrossWardSpectatorPlayers = () => {
  const {gameId, locked, spectators, me: {isSpectator}, phase} = useGame();

  return (
    <Paper square sx={t => ({padding: t.spacing(), minHeight: '44px'})}>
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
            : <BecomeSpectatorButton gameId={gameId} phase={phase}/>}
        </AvatarGroup>
      </Stack>
    </Paper>
  );
};
