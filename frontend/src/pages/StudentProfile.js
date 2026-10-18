import React, { useEffect, useState } from 'react';
import StudentCard from '../components/StudentCard';
import CompetencyRadarChart from '../components/CompetencyRadarChart';
import { getStudentById, getStudentCompetencies } from '../services/api';
import { formatCompetenciesForRadar, summarizeCompetencies, MAX_LEVEL } from '../utils/chartData';
import './Students.css';

export default function StudentProfile({ studentId, onBack }) {
    const [student, setStudent] = useState(null);
    const [radarData, setRadarData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        setLoading(true);
        setError(null);

        // The competency profile API is keyed by student number, so load the student first
        getStudentById(studentId)
            .then((studentResponse) => {
                setStudent(studentResponse.data);
                return getStudentCompetencies(studentResponse.data.numeroEstudante);
            })
            .then((competenciesResponse) => {
                setRadarData(formatCompetenciesForRadar(competenciesResponse.data));
            })
            .catch((err) => {
                setError(err.response?.status === 404
                    ? 'Student not found.'
                    : 'Could not load the student profile. Please try again.');
            })
            .finally(() => setLoading(false));
    }, [studentId]);

    const summary = summarizeCompetencies(radarData);

    return (
        <main className="students-page">
            {onBack && (
                <button type="button" className="link-button" onClick={onBack}>
                    ← Back to students
                </button>
            )}

            <h2>Student Profile</h2>

            {loading && <p>Loading profile...</p>}
            {error && <p className="error-message">{error}</p>}

            {!loading && !error && student && (
                <>
                    <StudentCard
                        student={{
                            name: student.nome,
                            number: student.numeroEstudante,
                            degree: student.curso,
                        }}
                    />

                    <section className="profile-section">
                        <h3>Competency Map</h3>
                        <CompetencyRadarChart data={radarData} />

                        {radarData.length > 0 && (
                            <div className="competency-summary">
                                <div>
                                    <span className="summary-label">Average level</span>
                                    <strong>{summary.average} / {MAX_LEVEL}</strong>
                                </div>
                                <div>
                                    <span className="summary-label">Strongest</span>
                                    <strong>{summary.strongest}</strong>
                                </div>
                                <div>
                                    <span className="summary-label">Needs improvement</span>
                                    <strong>{summary.weakest}</strong>
                                </div>
                            </div>
                        )}
                    </section>
                </>
            )}
        </main>
    );
}
