import {theme} from '@app/styles/theme';
import {ThemeProvider} from '@mui/material';
import {SnackbarProvider} from 'notistack';
import {BrowserRouter} from 'react-router-dom';

export const AppProviders = ({children}) => (
  <BrowserRouter basename="/ui">
    <ThemeProvider theme={theme}>
      <SnackbarProvider>
        {children}
      </SnackbarProvider>
    </ThemeProvider>
  </BrowserRouter>
);
