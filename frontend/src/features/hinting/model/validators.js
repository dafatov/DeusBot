const WORD_REGEX = /^[A-Za-zА-Яа-я]+$/;

export const isValidWord = word => word.trim().length > 0 && WORD_REGEX.test(word);

export const isValidCount = (count) => {
  if (count.trim() === '') return false;
  const num = Number(count);
  return Number.isInteger(num) && num >= 0 && num < 10;
};
