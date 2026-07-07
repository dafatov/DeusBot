import {alpha, Box, Slider} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {useWordsStats} from '../model/hooks/useWordsStats';

const CustomMark = ({'data-index': index, style, ownerState: {marks}}) =>
  <span style={{
    ...style,
    position: 'absolute',
    width: '8px',
    height: '8px',
    borderRadius: '8px',
    top: '50%',
    backgroundColor: marks[index].color,
    transform: 'translate(-1px, -50%)',
  }}/>;

export const SliderControl = ({value, onChange}) => {
  const {words} = useGame();
  const {maxOrder, marks, teamRoundLinearGradient} = useWordsStats(words);

  return (
    <Box sx={t => ({
      width: '100%',
      marginRight: '-1px',
      paddingLeft: t.spacing(2),
      paddingRight: t.spacing(2),
      paddingTop: '3px',
      backgroundColor: alpha(t.palette.background.paper, 0.25),
      borderRadius: t.spacing(0.5),
      border: `solid 1px ${t.palette.primary.main}`,
    })}>
      <Slider
        disabled={!marks?.length}
        slots={{mark: CustomMark}}
        slotProps={{
          rail: {style: {background: teamRoundLinearGradient, opacity: 1}},
          thumb: {style: {backgroundColor: marks.find(m => m.value === (value ?? maxOrder))?.color}}
        }}
        track={false}
        value={value ?? maxOrder}
        onChange={(_, v) => onChange(v === maxOrder ? undefined : v)}
        valueLabelDisplay="auto"
        defaultValue={maxOrder}
        marks={marks}
        min={0}
        max={maxOrder}
      />
    </Box>
  );
};
