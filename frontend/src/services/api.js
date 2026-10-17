import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

export const getStatus = () => api.get('/status');

export const getCourseUnits = () => api.get('/course-units');
export const getStudentEnrollments = (studentId) => api.get(`/estudantes/${studentId}/enrollments`);
export const registerEnrollment = (studentId, data) => api.post(`/estudantes/${studentId}/enrollments`, data);

export default api;