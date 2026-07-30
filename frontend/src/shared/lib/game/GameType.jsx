import {CodeNamesContent} from '@pages/code-names/CodeNames';
import {CrossWardContent} from '@pages/cross-ward/CrossWard';
import {GameLobby} from '@pages/game-lobby/CodeNamesLobby';
import {getUserId} from '../cookies';
import {GameProvider} from './GameProvider';

export const GameType = ({
  'code-names': {
    lobby: () => <GameLobby gameType="code_names" gameUrlPart="code-names" gameName="Codenames"/>,
    component: CodeNamesContent,
    provider: props => <GameProvider {...props} gameType="code_names" transform={game => {
      const me = (game?.playerList ?? []).find(p => getUserId() === p?.id);

      return {
        gameId: game?.key,
        score: game?.state?.score,
        team: game?.state?.team,
        phase: game?.state?.phase,
        locked: game?.state?.locked,
        paused: !!game?.timer?.remaining,
        timer: game?.timer?.timer ?? game?.timer?.remaining,
        spectators: (game?.playerList ?? []).filter(p => p.team === 'SPECTATOR'),
        playersBySkip: (game?.playerList ?? []).filter(p => game?.voteMap?.[p.id]?.type === 'skip'),
        words: game?.wordList ?? [],
        me: {
          isSpectator: me?.team === 'SPECTATOR',
          team: me?.team,
          isCaptain: me?.captain,
          isHost: game?.hostId === getUserId(),
        },
        findCaptain: team => (game?.playerList ?? []).find(p => p.team === team && p.captain),
        filterPlayers: team => (game?.playerList ?? []).filter(p => p.team === team && !p.captain),
        filterHints: team => (game?.hintList ?? []).filter(h => h.team === team),
        filterPlayersByWord: word => (game?.playerList ?? []).filter(p => game?.voteMap?.[p.id]?.word === word?.text),
      };
    }}/>,
  },
  'cross-ward': {
    lobby: () => <GameLobby gameType="cross_ward" gameUrlPart="cross-ward" gameName="Crossward"/>,
    component: CrossWardContent,
    provider: props => <GameProvider {...props} gameType="cross_ward" transform={game => {
      console.log({game});

      return {
        gameId: game?.key,
      };
    }}/>,
  },
});
