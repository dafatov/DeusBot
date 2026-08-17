import {useGame} from '@entities/game/lib/hooks';
import {
  AbcOutlined,
  Church,
  DisabledVisible,
  ExpandLess,
  ExpandMore,
  LocationSearching,
  Man4,
  Microsoft,
  Send,
  Visibility
} from '@mui/icons-material';
import {Collapse, IconButton, List, ListItem, ListItemAvatar, ListItemButton, ListItemIcon, ListItemText, Paper} from '@mui/material';
import {DiscordAvatar} from '@shared/ui/DiscordAvatar';
import {useState} from 'react';
import {FREQUENCY_RADIUS} from '../../spell-zone/ui/Radar';

export const HistoryZone = ({historyVisible, setHistoryVisible}) => {
  const {grid: {shift}, history, findPlayer, letterTags} = useGame();
  const [opens, setOpens] = useState({});
  const [historyTimer, setHistoryTimer] = useState(0);

  const setHistoryVisible2 = (e) => {
    if (historyTimer) {
      clearTimeout(historyTimer);
    }
    setHistoryVisible(e);
    if (e) {
      setHistoryTimer(setTimeout(() => setHistoryVisible(null), 2000));
    }
  };

  const getEventContent = color => (action, i) => {
    switch (action.type) {
      case 'cross_ward.use_spell':
        switch (action.spell.type) {
          case 'radar':
            return <ListItem
              key={i}
              secondaryAction={
                <IconButton edge="end" aria-label="show" onClick={() => setHistoryVisible2(historyVisible?.i === i ? null : {
                  i, ...{
                    type: 'area',
                    color,
                    x: action.spell.x - shift.x,
                    y: action.spell.y - shift.y,
                    radius: FREQUENCY_RADIUS[['FREQUENCY_HIGH', 'FREQUENCY_MEDIUM', 'FREQUENCY_LOW'].find(t => letterTags[t].includes(action.spell.letter))]
                  }
                })}>
                  {historyVisible?.i === i ? <DisabledVisible/> : <Visibility/>}
                </IconButton>
              }>
              <ListItemIcon>
                <LocationSearching/>
              </ListItemIcon>
              <ListItemText>{action.spell.letter}</ListItemText>
            </ListItem>;
          case 'loner':
            return <ListItem
              key={i}
              secondaryAction={
                <IconButton edge="end" aria-label="show" onClick={() => setHistoryVisible2(historyVisible?.i === i ? null : {
                  i, ...{
                    type: 'area',
                    color,
                    x: action.spell.x - shift.x,
                    y: action.spell.y - shift.y,
                    radius: 5
                  }
                })}>
                  {historyVisible?.i === i ? <DisabledVisible/> : <Visibility/>}
                </IconButton>
              }>
              <ListItemIcon>
                <Man4/>
              </ListItemIcon>
            </ListItem>;
          case 'crucifix':
            return <ListItem
              key={i}
              secondaryAction={
                <IconButton edge="end" aria-label="show" onClick={() => setHistoryVisible2(historyVisible?.i === i ? null : {
                  i, ...{
                    type: 'area',
                    color,
                    x: action.spell.x - shift.x,
                    y: action.spell.y - shift.y,
                    radius: 0
                  }
                })}>
                  {historyVisible?.i === i ? <DisabledVisible/> : <Visibility/>}
                </IconButton>
              }>
              <ListItemIcon>
                <Church/>
              </ListItemIcon>
            </ListItem>;
          case 'echo':
            return <ListItem
              key={i}
              secondaryAction={
                <IconButton edge="end" aria-label="show"
                            onClick={() => setHistoryVisible2(historyVisible?.i === i ? null : {i, ...{type: 'word', wordId: action.spell.wordId}})}>
                  {historyVisible?.i === i ? <DisabledVisible/> : <Visibility/>}
                </IconButton>
              }>
              <ListItemIcon>
                <AbcOutlined/>
              </ListItemIcon>
            </ListItem>;
          case 'crosslight':
            return <ListItem
              key={i}
              secondaryAction={
                <IconButton edge="end" aria-label="show"
                            onClick={() => setHistoryVisible2(historyVisible?.i === i ? null : {i, ...{type: 'word', wordId: action.spell.wordId}})}>
                  {historyVisible?.i === i ? <DisabledVisible/> : <Visibility/>}
                </IconButton>
              }>
              <ListItemIcon>
                <Microsoft/>
              </ListItemIcon>
            </ListItem>;
        }

        throw new Error(`Missing spell type: ${action.spell.type}`);
      case 'cross_ward.submit_word':
        return <ListItem
          key={i}
          secondaryAction={
            <IconButton edge="end" aria-label="show"
                        onClick={() => setHistoryVisible2(historyVisible?.i === i ? null : {i, ...{type: 'word', wordId: action.wordId}})}>
              {historyVisible?.i === i ? <DisabledVisible/> : <Visibility/>}
            </IconButton>
          }>
          <ListItemIcon>
            <Send/>
          </ListItemIcon>
          <ListItemText>{action.word}</ListItemText>
        </ListItem>;
    }

    return null;
  };

  return (
    <Paper sx={{
      position: 'absolute',
      top: '50%',
      transform: 'translateY(-50%)',
      left: 16,
      zIndex: 2,
      minWidth: '200px',
      opacity: 0.8,
    }}>
      <List>
        {history
          .map(g => ({player: findPlayer(g.issuerId), actions: g.actions}))
          .map(g => ({player: g.player, actions: g.actions.map(getEventContent(g.player.color)).filter(g => g)}))
          .filter(g => g.player && g.actions?.length > 0)
          .map(({player, actions}) => (
            <>
              <ListItemButton onClick={() => setOpens(o => ({...o, [player.id]: !o[player.id]}))} key={player.id}>
                <ListItemAvatar>
                  <DiscordAvatar
                    id={player.id}
                    name={player.name}
                    avatar={player.avatar}
                    disconnected={player.disconnected}
                  />
                </ListItemAvatar>
                {opens[player.id] ? <ExpandLess/> : <ExpandMore/>}
              </ListItemButton>
              <Collapse in={opens[player.id]} timeout="auto" unmountOnExit>
                <List component="div">
                  {actions}
                </List>
              </Collapse>
            </>
          ))}
      </List>
    </Paper>
  );
};
