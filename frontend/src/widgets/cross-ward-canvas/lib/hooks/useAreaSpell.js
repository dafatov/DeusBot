import {useCallback} from 'react';

export const useAreaSpell = (onAriaSpellClick) => {
  const onCellClick = useCallback(data => {
    if (!data) {
      return;
    }

    onAriaSpellClick(data);
  }, [onAriaSpellClick]);

  return {onCellClick};
};
