import {Send} from '@mui/icons-material';
import {FREQUENCY_RADIUS, SPELL_ICON} from '../../model/crossward/constants';

export const buildHistoryItems = (
  setHighlight,
  currentHighlight,
  findPlayer,
  shift,
  letterTags,
  actions
) => {
  if (!actions) return [];

  return actions
    .map((action) => {
      const props = buildItemProps(
        action.action,
        action.id,
        setHighlight,
        currentHighlight,
        findPlayer(action.issuerId),
        shift,
        letterTags
      );
      if (!props?.icon) return null;
      return {key: action.id, ...props};
    })
    .filter(Boolean);
};

const buildItemProps = (action, id, setHighlight, currentHighlight, player, shift, letterTags) => {
  if (action.type === 'cross_ward.use_spell') {
    return buildSpellProps(action, id, setHighlight, currentHighlight, player, shift, letterTags);
  }
  if (action.type === 'cross_ward.submit_word') {
    return buildSubmitWordProps(action, id, setHighlight, currentHighlight, player);
  }
  return null;
};

const buildSpellProps = (action, id, setHighlight, currentHighlight, player, shift, letterTags) => {
  const isActive = currentHighlight?.id === id;
  const payload = getVisibilityPayloadForSpell(action.spell, player?.color, shift, letterTags);
  const highlightData = isActive ? null : {id, ...payload};
  const onClick = () => setHighlight(highlightData);

  return {
    icon: SPELL_ICON[action.spell.type],
    text: payload?.text,
    show: isActive,
    onClick,
    player,
  };
};

const buildSubmitWordProps = (action, id, setHighlight, currentHighlight, player) => {
  const isActive = currentHighlight?.id === id;
  const highlightData = isActive ? null : {id, type: 'word', wordId: action.wordId};
  const onClick = () => setHighlight(highlightData);

  return {
    icon: Send,
    text: action.word,
    show: isActive,
    onClick,
    player,
  };
};

const getVisibilityPayloadForSpell = (spell, color, shift, letterTags) => {
  switch (spell.type) {
    case 'radar': {
      const frequencyKey = ['FREQUENCY_HIGH', 'FREQUENCY_MEDIUM', 'FREQUENCY_LOW']
        .find(t => letterTags[t].includes(spell.letter));
      return {
        type: 'area',
        color,
        x: spell.x - shift.x,
        y: spell.y - shift.y,
        radius: FREQUENCY_RADIUS[frequencyKey],
        text: spell.letter,
      };
    }
    case 'loner':
      return {type: 'area', color, x: spell.x - shift.x, y: spell.y - shift.y, radius: 5};
    case 'crucifix':
      return {type: 'area', color, x: spell.x - shift.x, y: spell.y - shift.y, radius: 0};
    case 'echo':
    case 'crosslight':
      return {type: 'word', wordId: spell.wordId};
    default:
      throw new Error(`Unknown spell type: ${spell.type}`);
  }
};
