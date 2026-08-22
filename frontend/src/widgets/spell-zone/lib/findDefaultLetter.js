import {FREQUENCY_RADIUS} from '../model/constants';

export const findDefaultLetterAndRadius = letterTags => {
  if (!letterTags) {
    return [null, null];
  }

  const entry = Object.entries(letterTags).find(([, letters]) => letters?.length > 0);

  return entry ? [entry[1][0], FREQUENCY_RADIUS[entry[0]]] : [null, null];

};
