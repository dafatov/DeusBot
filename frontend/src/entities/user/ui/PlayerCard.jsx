import {ListItem, ListItemAvatar, ListItemText} from '@mui/material';
import {DiscordAvatar} from '@shared/ui/DiscordAvatar';

export const PlayerCard = ({player}) => {
  return (
    <ListItem key={player.id}>
      <ListItemAvatar>
        <DiscordAvatar
          id={player.id}
          name={player.name}
          avatar={player.avatar}
          disconnected={player.disconnected}
        />
      </ListItemAvatar>
      <ListItemText primary={player.name}/>
    </ListItem>
  );
};
