import {Grid} from '@mui/material';
import {useGame} from '@shared/lib/game/hooks';
import {useState} from 'react';
import {SliderControl} from './SliderControl';
import {WordCard} from './WordCard';

export const WordsGrid = () => {
  const {words} = useGame();
  const [lastCardIndex, setLastCardIndex] = useState(undefined);

  const isVisible = word => lastCardIndex === undefined || word?.revealed?.order <= lastCardIndex;

  return (
    <Grid
      container
      spacing={2}
      columns={Math.sqrt(words.length)}
      sx={{justifyContent: 'center', alignItems: 'center', maxWidth: '50vw'}}
    >
      {words.map(word => (
        <Grid item size={1} sx={{aspectRatio: '3/2'}}>
          <WordCard word={word} visible={isVisible(word)}/>
        </Grid>
      ))}
      {words?.length > 0 && <SliderControl value={lastCardIndex} onChange={setLastCardIndex}/>}
    </Grid>
  );
};
