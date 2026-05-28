import {ContentCopyOutlined} from '@mui/icons-material';
import {Stack, Tooltip, Typography} from '@mui/material';
import {useState} from 'react';

export const CopyText = ({text}) => {
  const [animate, setAnimate] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(text)
      .then(() => {
        setAnimate(true);
        setTimeout(() => setAnimate(false), 300);
      });
  };

  return (
    <Tooltip disableInteractive title={animate ? 'Скопировано' : 'Нажми чтобы скопировать'}>
      <Stack direction="row" spacing={1} sx={{alignItems: 'center', cursor: 'pointer'}} onClick={handleCopy}>
        <Typography
          color="primary"
          variant="h4"
          sx={t => ({color: animate ? t.palette.success.main : undefined})}
        >
          {text}
        </Typography>
        <ContentCopyOutlined sx={t => ({color: animate ? t.palette.success.main : t.palette.primary.main})}/>
      </Stack>
    </Tooltip>
  );
};
