import {DeleteOutlined, UploadFileOutlined} from '@mui/icons-material';
import {Button, IconButton, InputLabel, MenuItem, Select, Stack, Tooltip, Typography} from '@mui/material';
import {useSnackbar} from '@shared/lib/snackbar/hooks';
import {useEffect, useRef, useState} from 'react';
import {deletePack, fetchPacks, uploadPacks} from '../model/packApi';
import {VisuallyHiddenInput} from './VisuallyHiddenInput';

export const PackSelector = ({packId, onChange}) => {
  const [packs, setPacks] = useState([]);
  const {showError} = useSnackbar();
  const [isLoading, setIsLoading] = useState(false);
  const fileInputRef = useRef(null);

  const handleUploadPack = files => {
    if (!files || files.length === 0) {
      return;
    }

    setIsLoading(true);
    uploadPacks(files).then(setPacks).catch(e => showError(e.message)).finally(() => {
      setIsLoading(false);

      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    });
  };

  const handleDeletePack = id => {
    if (window.confirm('Вы точно уверены что хотите безвозвратно удалить?')) {
      deletePack(id).then(setPacks).catch(e => showError(e.message));
    }
  };

  useEffect(() => {
    fetchPacks().then(setPacks).catch(e => showError(e.message));
  }, []);

  return (
    <Stack direction="row">
      <InputLabel>Набор слов</InputLabel>
      <Select
        value={packs.find(p => p.id === packId) ?? ''}
        onChange={e => onChange(e.target.value.id)}
        label="Набор слов"
        variant="outlined"
        sx={t => ({marginBottom: t.spacing(1), flexGrow: 1})}
        renderValue={p => <>{p.name} ({p.count} слов)</>}
      >
        {packs.map(pack => (
          <MenuItem key={pack.id} value={pack}>
            {pack.name} ({pack.count} слов)
            <IconButton
              sx={{marginLeft: 'auto'}}
              onClick={e => {
                e.stopPropagation();
                handleDeletePack(pack.id);
              }}
            >
              <DeleteOutlined/>
            </IconButton>
          </MenuItem>
        ))}
      </Select>
      <Tooltip
        arrow
        placement="right"
        title={<>
          <Typography>
            Просто текстовый документ, в котором каждое отдельное слово идет с новой строки
          </Typography>
        </>}
      >
        <Button
          loading={isLoading}
          component="label"
          variant="outlined"
          sx={t => ({marginBottom: t.spacing(1), marginLeft: t.spacing(1)})}
        >
          <UploadFileOutlined/>
          <VisuallyHiddenInput
            ref={fileInputRef}
            multiple
            type="file"
            name="files"
            onChange={e => handleUploadPack(e.target.files)}
          />
        </Button>
      </Tooltip>
    </Stack>
  );
};
