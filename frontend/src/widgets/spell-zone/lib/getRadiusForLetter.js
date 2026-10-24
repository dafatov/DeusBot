import {FREQUENCY_RADIUS} from '@entities/game/model/crossward/constants';

export const getRadiusForLetter = (letter, letterTags) => {
  if (!letter || !letterTags) {
    return null;
  }

  for (const [key, letters] of Object.entries(letterTags)) {
    if (letters.includes(letter)) {
      return FREQUENCY_RADIUS[key];
    }
  }

  return null;
};
