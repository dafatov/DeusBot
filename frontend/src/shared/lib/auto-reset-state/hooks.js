import {useCallback, useRef, useState} from 'react';

export const useAutoResetState = delay => {
  const [value, setValueState] = useState(null);
  const timerRef = useRef(null);

  const clearTimer = useCallback(() => {
    if (!timerRef.current) {
      return;
    }

    clearTimeout(timerRef.current);
    timerRef.current = null;
  }, []);

  const setValue = useCallback(newValue => {
    clearTimer();
    setValueState(newValue);

    if (newValue === null) {
      return;
    }

    timerRef.current = setTimeout(() => {
      setValueState(null);
      timerRef.current = null;
    }, delay);
  }, [clearTimer, delay]);

  return [value, setValue];
};
