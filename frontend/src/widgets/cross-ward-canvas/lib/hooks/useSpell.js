import {useCallback} from 'react';
import {getWordId} from '../utils/getWordInfo';


export const useSpell = onSpellClick => {
  const onCellClick = useCallback(data => {
    if (!data) {
      return;
    }

    onSpellClick({...data, wordId: getWordId(data)});
  }, [onSpellClick]);

  return {onCellClick};
};
