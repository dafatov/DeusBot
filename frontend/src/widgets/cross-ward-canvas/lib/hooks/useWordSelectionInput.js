import {useGameSessionStorage} from '@entities/game/lib/hooks';
import {useCallback, useEffect, useMemo} from 'react';

const clearSelectedManualLetters = (saveManualLetters, sortedCells) => {
  saveManualLetters(manualLetters => sortedCells?.reduce((acc, c) => {
    const {[`${c.x},${c.y}`]: _, ...rest} = acc;
    return rest;
  }, manualLetters));
};

export const useWordSelectionInput = (startedAt, selectedWord, words, cells, shift, onWordSubmit) => {
  const [storageValue, setStorageValue] = useGameSessionStorage('manualLetters', {});

  const manualLetters = useMemo(() => (storageValue?.started_at === startedAt && storageValue?.data) || {}
    , [storageValue, startedAt]);

  const saveManualLetters = useCallback(arg => {
    const newLetters = typeof arg === 'function' ? arg(manualLetters) : arg;

    if (newLetters === manualLetters) {
      return;
    }

    setStorageValue({started_at: startedAt, data: newLetters});
  }, [startedAt, setStorageValue, manualLetters]);

  useEffect(() => {
    if (storageValue && storageValue.started_at !== startedAt) {
      saveManualLetters({});
    }
  }, [startedAt, storageValue, saveManualLetters]);

  const sortedCells = useMemo(() => words[selectedWord]?.cells
      .toSorted((a, b) => a.x === b.x ? a.y - b.y : a.x - b.x)
    , [words[selectedWord]]);

  const activeCell = useMemo(() => sortedCells?.find(c => !manualLetters[`${c.x},${c.y}`] && !cells.get(`${c.x - shift.x},${c.y - shift.y}`)?.revealed)
    , [sortedCells, cells, manualLetters, shift]);

  useEffect(() => {
    saveManualLetters(manualLetters => {
      const keysToRemove = Object.keys(manualLetters).filter(key => {
        const [x, y] = key.split(',');

        return cells.get(`${x - shift.x},${y - shift.y}`)?.revealed === true;
      });

      return keysToRemove.reduce((acc, key) => {
        const {[key]: _, ...rest} = acc;
        return rest;
      }, manualLetters);
    });
  }, [cells, shift, saveManualLetters]);

  const onLetterDown = useCallback(letter => {
    if (!selectedWord) return;

    const word = words[selectedWord];

    if (!word || word.revealed || !activeCell) return;

    saveManualLetters(manualLetters => ({...manualLetters, [`${activeCell.x},${activeCell.y}`]: letter.toLowerCase()}));
  }, [selectedWord, words, activeCell, saveManualLetters]);

  const onBackspaceDown = useCallback(isCtrl => {
    if (!selectedWord) return;

    const word = words[selectedWord];

    if (!word) return;

    if (isCtrl) {
      clearSelectedManualLetters(saveManualLetters, sortedCells);
      return;
    }

    saveManualLetters(manualLetters => {
      const lastCell = sortedCells?.findLast(cell => Object.hasOwn(manualLetters, `${cell.x},${cell.y}`));

      if (!lastCell) return manualLetters;

      const {[`${lastCell.x},${lastCell.y}`]: _, ...rest} = manualLetters;

      return rest;
    });
  }, [selectedWord, words, sortedCells, saveManualLetters]);

  const onSpaceDown = useCallback(isCtrl => {
    if (isCtrl) {
      onBackspaceDown(isCtrl);
    }
  }, [onBackspaceDown]);

  const onEnterDown = useCallback(() => {
    const word = sortedCells?.map(c => cells.get(`${c.x - shift.x},${c.y - shift.y}`)?.letter || manualLetters[`${c.x},${c.y}`])?.join('');

    if (!word || word?.length !== sortedCells?.length) {
      return;
    }

    onWordSubmit(selectedWord, word)
      .then(() => clearSelectedManualLetters(saveManualLetters, sortedCells));
  }, [sortedCells, manualLetters, cells, selectedWord, onWordSubmit, saveManualLetters]);

  return {onLetterDown, onBackspaceDown, onSpaceDown, onEnterDown, manualLetters, activeCell};
};
