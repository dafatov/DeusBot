import {useEffect} from 'react';

export const usePageTitle = (title = '<Unknown>') => {
  useEffect(() => {
    const oldTitle = document.title;

    document.title = title;
    return () => {
      document.title = oldTitle;
    };
  }, [title]);
};
