import {alpha, AvatarGroup, Box, Divider, Tooltip} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {useSocket} from '@shared/lib/socket/hooks';
import {DiscordAvatar} from '@shared/ui/DiscordAvatar';
import {sendVote} from '../model/voteService';

export const VoteZone = ({children, playerList, vote, clickable = true}) => {
  const {gameId} = useGame();
  const {send} = useSocket();

  return (
    <Box onClick={clickable ? () => sendVote(send, gameId, vote) : undefined} sx={t => ({
      display: 'flex',
      flexDirection: 'column',
      padding: t.spacing(1),
      height: `calc(100% - ${t.spacing(2)})`,
      cursor: clickable ? 'pointer' : 'inherit',
      gap: t.spacing(0.5),
    })}>
      {children}
      {playerList.length > 0
        ? <>
          <Divider sx={t => ({background: alpha(t.palette.primary.main, 0.01)})}/>
          <AvatarGroup max={Infinity}>
            {playerList.map(p => (
              <Tooltip arrow disableInteractive title={p.name}>
                <DiscordAvatar mini id={p.id} name={p.name} avatar={p.avatar} disconnected={p.disconnected}/>
              </Tooltip>
            ))}
          </AvatarGroup>
        </>
        : <Box sx={t => ({minHeight: `calc(20px + 1px + ${t.spacing(0.5)})`})}></Box>}
    </Box>
  );
};
