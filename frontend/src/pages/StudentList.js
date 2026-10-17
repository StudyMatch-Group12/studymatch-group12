import React, { useEffect, useState } from 'react';
import { getCourses, searchStudents } from '../services/api';
import { DEFAULT_FILTERS, SORT_OPTIONS } from '../utils/studentFilters';
import './Students.css';

const SEARCH_DEBOUNCE_MS = 300;

export default function StudentList({ onSelectStudent }) {
    const [filters, setFilters] = useState(DEFAULT_FILTERS);
    const [students, setStudents] = useState([]);
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        getCourses()
            .then((response) => setCourses(response.data))
            .catch(() => setCourses([]));
    }, []);

    // Wait until the user stops typing before calling the API
    useEffect(() => {
        let cancelled = false;
        const timer = setTimeout(() => {
            setLoading(true);
            searchStudents(filters)
                .then((response) => {
                    if (!cancelled) {
                        setStudents(response.data);
                        setError(null);
                    }
                })
                .catch((err) => {
                    if (!cancelled) {
                        setStudents([]);
                        setError(typeof err.response?.data === 'string'
                            ? err.response.data
                            : 'Could not load students. Please try again.');
                    }
                })
                .finally(() => {
                    if (!cancelled) setLoading(false);
                });
        }, SEARCH_DEBOUNCE_MS);

        return () => {
            cancelled = true;
            clearTimeout(timer);
        };
    }, [filters]);

    const handleChange = (e) => {
        setFilters({ ...filters, [e.target.name]: e.target.value });
    };

    const toggleDirection = () => {
        setFilters({ ...filters, direction: filters.direction === 'asc' ? 'desc' : 'asc' });
    };

    return (
        <main className="students-page">
            <h2>Students</h2>

            <div className="filters-bar">
                <input
                    type="search"
                    name="search"
                    aria-label="Search students"
                    placeholder="Search by name or student number"
                    value={filters.search}
                    onChange={handleChange}
                />

                <select name="course" aria-label="Filter by course" value={filters.course} onChange={handleChange}>
                    <option value="">All courses</option>
                    {courses.map((course) => (
                        <option key={course} value={course}>{course}</option>
                    ))}
                </select>

                <select name="sortBy" aria-label="Sort by" value={filters.sortBy} onChange={handleChange}>
                    {SORT_OPTIONS.map((option) => (
                        <option key={option.value} value={option.value}>Sort by: {option.label}</option>
                    ))}
                </select>

                <button type="button" onClick={toggleDirection} aria-label="Toggle sort direction">
                    {filters.direction === 'asc' ? '↑ Ascending' : '↓ Descending'}
                </button>

                <button type="button" className="link-button" onClick={() => setFilters(DEFAULT_FILTERS)}>
                    Clear filters
                </button>
            </div>

            {error && <p className="error-message">{error}</p>}
            {loading && <p>Loading students...</p>}

            {!loading && !error && (
                <>
                    <p className="results-count">{students.length} student(s) found</p>
                    {students.length > 0 && (
                        <table className="students-table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Student number</th>
                                    <th>Course</th>
                                    <th>Email</th>
                                </tr>
                            </thead>
                            <tbody>
                                {students.map((student) => (
                                    <tr
                                        key={student.id}
                                        onClick={() => onSelectStudent?.(student.id)}
                                        title="Open profile"
                                    >
                                        <td>{student.nome}</td>
                                        <td>{student.numeroEstudante}</td>
                                        <td>{student.curso}</td>
                                        <td>{student.email}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </>
            )}
        </main>
    );
}
