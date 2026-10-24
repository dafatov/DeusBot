export const getCellEdgeColor = (isVertical, current, neighbor, hasAnotherNeighbor) => {
  const aH = current?.words?.HORIZONTAL;
  const aV = current?.words?.VERTICAL;
  const bH = neighbor?.words?.HORIZONTAL;
  const bV = neighbor?.words?.VERTICAL;
  const mask = (aH ? 8 : 0) | (aV ? 4 : 0) | (bH ? 2 : 0) | (bV ? 1 : 0);
  const fromCurrent = isVertical ? aV : aH;
  const fromNeighbor = isVertical ? bV : bH;

  switch (mask) {
    case 15:
      return [
        hasAnotherNeighbor ? fromNeighbor : fromCurrent,
        hasAnotherNeighbor ? fromCurrent : fromNeighbor,
      ];

    case 14:
    case 5:
    case 4:
      return aV;

    case 13:
    case 10:
    case 8:
      return aH;

    case 12:
      return isVertical ? aV : aH;

    case 11:
    case 1:
      return bV;

    case 7:
    case 2:
      return bH;

    case 3:
      return isVertical ? bV : bH;

    case 0:
      return null;
    default:
      console.error('Unresolvable mask:', mask);
      return undefined;
  }
};
