import {createTheme} from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#FFFF50',
      success: '#50ff50',
      failure: '#ff5050',
    },
    secondary: {
      main: '#0000af',
    },
  },
});
