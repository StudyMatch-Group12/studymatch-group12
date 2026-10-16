import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

export const getStatus = () => api.get('/status');
export const getCompetencyProfile = (numeroEstudante) =>
    api.get(`/competencias/estudantes/${numeroEstudante}`);
export default api;