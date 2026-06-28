import {Avatar, Badge} from '@mui/material';
import {styled} from '@mui/material/styles';

const StyledBadge = styled(Badge)(({theme}) => ({
  '& .MuiBadge-badge': {
    backgroundColor: '#44b700',
    color: '#44b700',
    boxShadow: `0 0 0 2px ${theme.palette.background.paper}`,
    '&::after': {
      position: 'absolute',
      width: '100%',
      height: '100%',
      borderRadius: '50%',
      animation: 'ripple 1.2s infinite ease-in-out',
      border: '1px solid currentColor',
      content: '""',
    },
  },
  '@keyframes ripple': {
    '0%': {
      transform: 'scale(.8)',
      opacity: 1,
    },
    '100%': {
      transform: 'scale(2.4)',
      opacity: 0,
    },
  },
}));


export const DiscordAvatar = ({id, name, avatar, mini, disconnected}) => {
  return (
    <StyledBadge
      overlap="circular"
      anchorOrigin={{vertical: 'bottom', horizontal: 'right'}}
      variant="dot"
      invisible={mini || disconnected}
    >
      <Avatar
        sx={mini ? {width: 16, height: 16} : {}}
        alt={name}
        src={`${import.meta.env.VITE_DISCORD_CDN_URL}/avatars/${id}/${avatar}.png`}
      />
    </StyledBadge>
  );
};
