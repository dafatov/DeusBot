import axios from 'axios';

const api = axios.create({
  baseURL: '/api/game/code-names',
  headers: {'Content-Type': 'application/json'},
});

export const gameApi = {
  createGame: (settings) => api.post('/create', settings)
    .then(res => res.data),

  joinGame: (gameId) => api.post(`/${gameId}/join`)
    .then(res => res.data),

  getPacks: () => api.get('/packs')
    .then(res => res.data),

  uploadPacks: (files) => {
    const formData = new FormData();

    for (const file of files) {
      formData.append('files', file);
    }

    return api.post('/packs', formData, {headers: {'Content-Type': 'multipart/form-data'},})
      .then(res => res.data);
  },

  deletePack: (id) => api.delete('/packs', {params: {id}})
    .then(res => res.data),
};
