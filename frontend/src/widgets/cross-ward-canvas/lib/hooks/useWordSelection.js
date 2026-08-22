import {useCallback} from 'react';
import {getWordId} from '../utils/getWordId';

export const useWordSelection = (setSelectedWord) => {
  const onCellClick = useCallback(data => {
    if (!data?.cell?.words) {
      setSelectedWord(null);
      return;
    }

    setSelectedWord(getWordId(data));
  }, [setSelectedWord]);

  return {onCellClick};
};
