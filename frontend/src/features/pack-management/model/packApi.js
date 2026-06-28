import {gameApi} from '@shared/api/gameApi';

/**
 * @param {FileList|File[]} files
 * @returns {Promise<Array>} - массив пакетов
 */
export const uploadPacks = files => gameApi.uploadPacks(files);

/**
 * @param {number} id
 * @returns {Promise<Array>} - массив пакетов
 */
export const deletePack = id => gameApi.deletePack(id);

/**
 * @returns {Promise<Array>} - массив пакетов
 */
export const fetchPacks = () => gameApi.getPacks();
