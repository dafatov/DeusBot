import {DisabledVisible, Visibility} from '@mui/icons-material';
import {Badge, IconButton, ListItem, ListItemIcon, ListItemText} from '@mui/material';
import {DiscordAvatar} from '@shared/ui/DiscordAvatar';

export const HistoryItem = ({icon: Icon, text, show, onClick, player}) => (
  <ListItem
    secondaryAction={
      <IconButton edge="end" onClick={onClick}>
        {show ? <DisabledVisible/> : <Visibility/>}
      </IconButton>
    }
  >
    <ListItemIcon>
      <Badge badgeContent={
        <DiscordAvatar
          mini
          id={player?.id}
          name={player?.name}
          avatar={player?.avatar}
        />
      }>
        <Icon/>
      </Badge>
    </ListItemIcon>
    {text && <ListItemText>{text}</ListItemText>}
  </ListItem>
);
