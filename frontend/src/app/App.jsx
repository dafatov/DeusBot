import {CircularProgress} from '@mui/material';
import {Suspense} from 'react';
import {AppView} from './AppView.jsx';
import {AppProviders} from './providers/AppProviders';

export const App = () => (
  <AppProviders>
    <Suspense fallback={<CircularProgress/>}>
      <AppView/>
    </Suspense>
  </AppProviders>
);
