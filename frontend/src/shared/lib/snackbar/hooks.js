import {enqueueSnackbar} from 'notistack';

const options = {
  anchorOrigin: {
    horizontal: 'right',
    vertical: 'top'
  }
};

export const useSnackbar = () => {
  return {
    showError: message => {
      if (typeof message !== 'string') {
        console.error(
          '[useSnackbar] showError ожидает строку, получено:',
          message,
          '\nУведомление не будет показано.'
        );
        return;
      }

      enqueueSnackbar(message, {variant: 'error', ...options});
    },
  };
};
