import {useGameSessionStorage} from '@entities/game/lib/hooks';
import {useCallback, useMemo} from 'react';

const clearSelectedManualLetters = (setManualLetters, sortedCells) => {
  setManualLetters(manualLetters => sortedCells?.reduce((acc, c) => {
    const {[`${c.x},${c.y}`]: _, ...rest} = acc;
    return rest;
  }, manualLetters));
};

export const useWordSelectionInput = (selectedWord, words, cells, shift, onWordSubmit) => {
  const [manualLetters, setManualLetters] = useGameSessionStorage('manualLetters', {});

  const sortedCells = useMemo(() => words[selectedWord]?.cells
      .toSorted((a, b) => a.x === b.x ? a.y - b.y : a.x - b.x)
      .map(c => ({...c, x: c.x - shift.x, y: c.y - shift.y}))
    , [words[selectedWord], shift]);

  const activeCell = useMemo(() => sortedCells?.find(c => !manualLetters[`${c.x},${c.y}`] && !cells.get(`${c.x},${c.y}`)?.revealed)
    , [sortedCells, cells, manualLetters]);

  const onLetterDown = useCallback(letter => {
    if (!selectedWord) return;

    const word = words[selectedWord];

    if (!word || word.revealed || !activeCell) return;

    setManualLetters(manualLetters => ({...manualLetters, [`${activeCell.x},${activeCell.y}`]: letter.toLowerCase()}));
  }, [selectedWord, words, activeCell, setManualLetters]);

  const onBackspaceDown = useCallback(isCtrl => {
    if (!selectedWord) return;

    const word = words[selectedWord];

    if (!word) return;

    if (isCtrl) {
      clearSelectedManualLetters(setManualLetters, sortedCells);
      return;
    }

    setManualLetters(manualLetters => {
      const lastCell = sortedCells?.findLast(cell => Object.hasOwn(manualLetters, `${cell.x},${cell.y}`));

      if (!lastCell) return manualLetters;

      const {[`${lastCell.x},${lastCell.y}`]: _, ...rest} = manualLetters;

      return rest;
    });
  }, [selectedWord, words, sortedCells, setManualLetters]);

  const onSpaceDown = useCallback(isCtrl => {
    if (isCtrl) {
      onBackspaceDown(isCtrl);
    }
  }, [onBackspaceDown]);

  const onEnterDown = useCallback(() => {
    const word = sortedCells?.map(c => manualLetters[`${c.x},${c.y}`] || cells.get(`${c.x},${c.y}`)?.letter)?.join('');

    if (!word || word?.length !== sortedCells?.length) {
      return;
    }

    onWordSubmit(selectedWord, word)
      .then(() => clearSelectedManualLetters(setManualLetters, sortedCells));
  }, [sortedCells, manualLetters, selectedWord, onWordSubmit, setManualLetters]);

  return {onLetterDown, onBackspaceDown, onSpaceDown, onEnterDown, manualLetters, activeCell};
};
