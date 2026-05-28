import {Card, CardContent, Stack, Typography} from '@mui/material';
import {useLocation} from 'react-router-dom';

export const NotFound = () => {
  const location = useLocation();

  return (
    <Card>
      <CardContent>
        <Stack direction="column" spacing={2}>
          <Typography align="center" variant="h1">Ошибка 404</Typography>
          <Typography>Страницы с адресом {location.pathname} не существует</Typography>
        </Stack>
      </CardContent>
    </Card>
  );
};
