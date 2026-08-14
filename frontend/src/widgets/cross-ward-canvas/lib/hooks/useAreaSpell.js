import {useCallback} from 'react';

export const useAreaSpell = (activeSpell, onAriaSpellClick) => {
  const onCellClick = useCallback(data => {
    if (!data) {
      return;
    }

    onAriaSpellClick(data);
  }, [activeSpell, onAriaSpellClick]);

  return {onCellClick};
};
