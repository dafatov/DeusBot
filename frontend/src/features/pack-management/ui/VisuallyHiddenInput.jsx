import {styled} from '@mui/material/styles';

export const VisuallyHiddenInput = styled('input')({
  clip: 'rect(0 0 0 0)',
  overflow: 'hidden',
  position: 'absolute',
});
