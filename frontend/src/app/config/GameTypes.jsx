import {GameLobbyForm} from '@entities/game';
import {GameProvider} from '@entities/game/lib/GameProvider';
import {transformCodeNames, transformCrossWard} from '@entities/game/lib/transforms';
import {CodeNamesContent} from '@widgets/code-names/CodeNamesContent';
import {CrossWardContent} from '@widgets/cross-ward/CrossWardContent';

export const GameTypes = ({
  'code-names': {
    lobby: () => <GameLobbyForm gameType="code_names" gameUrlPart="code-names" gameName="Codenames"/>,
    component: CodeNamesContent,
    provider: props => <GameProvider {...props} gameType="code_names" transform={transformCodeNames}/>,
  },
  'cross-ward': {
    lobby: () => <GameLobbyForm gameType="cross_ward" gameUrlPart="cross-ward" gameName="Crossward"/>,
    component: CrossWardContent,
    provider: props => <GameProvider {...props} gameType="cross_ward" transform={transformCrossWard}/>,
  },
});
