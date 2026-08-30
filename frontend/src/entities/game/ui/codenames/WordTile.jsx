import {Box, Stack, Typography} from '@mui/material';

export const WordTile = ({word}) => {
  return (
    <Stack direction="column" sx={{width: '100%', height: '100%', overflow: 'hidden'}}>
      <Box sx={t => ({
        flex: 1,
        overflowY: 'auto',
        display: 'flex',
        flexDirection: 'column',
        minHeight: 0,
        '&::-webkit-scrollbar': {
          width: t.spacing(0.5),
        },
        '&::-webkit-scrollbar-thumb': {
          background: t => t.palette.primary.main,
        },
        '&::-webkit-scrollbar-thumb:hover': {
          background: t => t.palette.primary.dark,
        },
      })}>
        <Typography variant="body1" color="primary" sx={t => ({
          padding: t.spacing(1),
          textTransform: 'uppercase',
          fontWeight: 'bold',
          wordBreak: 'break-word',
          overflowWrap: 'break-word',
          textAlign: 'center',
          marginTop: 'auto',
          marginBottom: 'auto',
        })}>
          {word.text}
        </Typography>
      </Box>
    </Stack>
  );
};
