export const getWordId = data => {
  if (data?.cell?.words?.VERTICAL && data?.cell?.words?.HORIZONTAL) {
    return data.cell.words[data.sector === 'E' || data.sector === 'W' ? 'HORIZONTAL' : 'VERTICAL'];
  } else {
    return Object.values(data?.cell?.words ?? {})?.[0];
  }
};
