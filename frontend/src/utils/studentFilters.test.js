import { buildStudentQueryParams, DEFAULT_FILTERS } from './studentFilters';

describe('buildStudentQueryParams', () => {
    test('sends only the default sort when no filter is filled in', () => {
        expect(buildStudentQueryParams(DEFAULT_FILTERS)).toEqual({ sortBy: 'name', direction: 'asc' });
    });

    test('trims values and drops empty, null and undefined ones', () => {
        const filters = { search: '  ana ', course: '   ', sortBy: 'course', direction: undefined, extra: null };

        expect(buildStudentQueryParams(filters)).toEqual({ search: 'ana', sortBy: 'course' });
    });

    test('returns an empty object when called without filters', () => {
        expect(buildStudentQueryParams()).toEqual({});
    });
});
