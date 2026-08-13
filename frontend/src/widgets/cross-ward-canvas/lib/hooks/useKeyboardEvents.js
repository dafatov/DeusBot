import {useCallback} from 'react';

export const useKeyboardEvents = (onLetterDown, onBackspaceDown, onSpaceDown, onEnterDown) => {
  const onKeyDown = useCallback(e => {
    e.preventDefault();

    if (e.key.length === 1 && /[a-zA-Zа-яА-Я]/.test(e.key)) {
      onLetterDown(e.key);
    }

    if (e.key === 'Backspace') {
      onBackspaceDown(e.ctrlKey);
    }

    if (e.key === ' ') {
      onSpaceDown(e.ctrlKey);
    }

    if (e.key === 'Enter') {
      onEnterDown();
    }
  }, [onLetterDown, onBackspaceDown, onSpaceDown, onEnterDown]);

  return {onKeyDown};
};
