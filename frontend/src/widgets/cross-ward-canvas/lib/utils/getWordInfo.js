export const getWordId = data => {
  return data?.cell?.words?.[getWordDirection(data)];
};

export const getWordDirection = data => {
  if (data?.cell?.words?.VERTICAL && data?.cell?.words?.HORIZONTAL) {
    return data.sector === 'E' || data.sector === 'W' ? 'HORIZONTAL' : 'VERTICAL';
  } else {
    return Object.keys(data?.cell?.words ?? {})?.[0];
  }
};
