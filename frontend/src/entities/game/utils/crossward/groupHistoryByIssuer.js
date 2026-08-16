export const groupHistoryByIssuer = history => {
  if (!history.length) return [];

  const groups = [];
  let currentIssuer = history[0].issuerId;
  let currentActions = [history[0].action];

  for (let i = 1; i < history.length; i++) {
    const item = history[i];

    if (item.issuerId === currentIssuer) {
      currentActions.push(item.action);
    } else {
      groups.push({issuerId: currentIssuer, actions: currentActions});
      currentIssuer = item.issuerId;
      currentActions = [item.action];
    }
  }

  groups.push({issuerId: currentIssuer, actions: currentActions});
  return groups;
};
