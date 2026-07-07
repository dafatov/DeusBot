import _ from 'lodash';
import {COLORS} from '../../config/colors';

const getOrder = word => word?.revealed?.order;

const format = (word, maxOrder) => `${COLORS[word.revealed.team][0]} ${(100 * getOrder(word) / maxOrder)}%`;

export const useWordsStats = words => {
  const revealedWord = words.filter(getOrder);
  const maxOrder = revealedWord.map(getOrder).reduce((max, o) => Math.max(max, o), 0);
  const marks = revealedWord.map(w => ({value: getOrder(w), label: '', color: COLORS[w.color][0]}));
  const teamRoundLinearGradient = _.chain(revealedWord)
    .groupBy(item => item.revealed.round)
    .flatMap(group => [format(_.minBy(group, getOrder), maxOrder), format(_.maxBy(group, getOrder), maxOrder)])
    .join(', ')
    .thru(g => `linear-gradient(90deg, ${g || 'currentcolor'})`)
    .value();

  return {maxOrder, marks, teamRoundLinearGradient};
};
