import {PackSelector} from '@features/pack-management';
import {Box, Button, Card, Divider, FormControl, Stack, TextField} from '@mui/material';
import {gameApi} from '@shared/api/gameApi';
import {useSnackbar} from '@shared/lib/snackbar/hooks';
import {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';

export const CodeNamesLobby = () => {
  const [setting, setSetting] = useState({});
  const [gameId, setGameId] = useState(null);
  const [createLoading, setCreateLoading] = useState(false);
  const [joinLoading, setJoinLoading] = useState(false);
  const navigate = useNavigate();
  const {showError} = useSnackbar();

  const handleCreate = () => {
    setCreateLoading(true);
    gameApi.createGame(setting).then(id => navigate(`/game/code-names/${id}`)).catch(e => showError(e.message)).finally(() => setCreateLoading(false));
  };

  const handleJoin = () => {
    setJoinLoading(true);
    gameApi.joinGame(gameId).then(() => navigate(`/game/code-names/${gameId}`)).catch(e => showError(e.message)).finally(() => setJoinLoading(false));
  };

  useEffect(() => {
    const oldTitle = document.title;

    document.title = 'Codenames';
    return () => {
      document.title = oldTitle;
    };
  }, []);

  return (
    <Box sx={{
      height: '100%',
      display: 'flex',
      'justify-content': 'center',
      'align-items': 'center',
    }}>
      <Card sx={t => ({padding: t.spacing(2)})}>
        <Stack direction="column" spacing={2}>
          <FormControl fullWidth={true}>
            <PackSelector packId={setting?.packId} onChange={id => setSetting({...setting, packId: id})}/>
            <Button loading={createLoading} disabled={!setting?.packId} variant="outlined" onClick={handleCreate}>Создать</Button>
          </FormControl>
          <Divider/>
          <Stack direction="row" spacing={2}>
            <TextField
              required
              label="Идентификатор"
              value={gameId}
              onChange={(e) => setGameId(e.target.value)}
            />
            <Button
              loading={joinLoading}
              disabled={!gameId}
              variant="outlined"
              onClick={handleJoin}
            >Подключиться</Button>
          </Stack>
        </Stack>
      </Card>
    </Box>
  );
};
