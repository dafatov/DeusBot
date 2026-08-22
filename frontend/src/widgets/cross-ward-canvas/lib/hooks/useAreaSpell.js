import {useCallback} from 'react';
import {getWordId} from '../utils/getWordId';


export const useAreaSpell = onSpellClick => {
  const onCellClick = useCallback(data => {
    if (!data) {
      return;
    }

    onSpellClick({...data, wordId: getWordId(data)});
  }, [onSpellClick]);

  return {onCellClick};
};
