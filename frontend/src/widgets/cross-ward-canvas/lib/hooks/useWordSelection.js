import {useCallback} from 'react';

export const useWordSelection = (setSelectedWord) => {
  const onCellClick = useCallback(data => {
    if (!data?.cell?.words) {
      setSelectedWord(null);
      return;
    }

    const {HORIZONTAL: h, VERTICAL: v} = data.cell.words;

    setSelectedWord(s => h && v
      ? (s === h ? v : h)
      : h || v);
  }, [setSelectedWord]);

  return {onCellClick};
};
