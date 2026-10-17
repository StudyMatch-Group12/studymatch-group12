export const SORT_OPTIONS = [
    { value: 'name', label: 'Name' },
    { value: 'number', label: 'Student number' },
    { value: 'course', label: 'Course' },
];

export const DEFAULT_FILTERS = {
    search: '',
    course: '',
    sortBy: 'name',
    direction: 'asc',
};

// Builds the query parameters for GET /api/estudantes, dropping empty values
// so the backend treats them as "no filter".
export function buildStudentQueryParams(filters = {}) {
    const params = {};
    Object.entries(filters).forEach(([key, value]) => {
        if (value === null || value === undefined) return;
        const trimmed = String(value).trim();
        if (trimmed !== '') {
            params[key] = trimmed;
        }
    });
    return params;
}
