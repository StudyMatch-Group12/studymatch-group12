import axios from 'axios';
import { buildStudentQueryParams } from '../utils/studentFilters';

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

// filters: { search, course, sortBy, direction } - empty values are not sent
export const searchStudents = (filters) =>
    api.get('/estudantes', { params: buildStudentQueryParams(filters) });

export const getCourses = () => api.get('/estudantes/courses');

export const getStudentById = (studentId) => api.get(`/estudantes/${studentId}`);

export const getStudentCompetencies = (numeroEstudante) =>
    api.get(`/competencias/estudantes/${numeroEstudante}`);

export default api;
