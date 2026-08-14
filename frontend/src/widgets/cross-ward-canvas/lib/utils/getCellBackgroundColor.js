export const getCellBackgroundColor = (cell, words) => {
  if (Object.values(cell?.words ?? {}).some(w => words[w]?.revealed)) {
    return '#00000020';
  }

  if (cell?.tags?.includes('ECHO_CONSONANT')) {
    return '#42aaff40';
  }

  if (cell?.tags?.includes('ECHO_VOWEL')) {
    return '#ffc0cb40';
  }

  if (Object.keys(cell?.words ?? {}).length > 0) {
    return '#ffffff20';
  }

  return '#00000000';
};
