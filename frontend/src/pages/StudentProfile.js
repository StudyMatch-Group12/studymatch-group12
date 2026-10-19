import React, { useEffect, useState } from 'react';
import StudentCard from '../components/StudentCard';
import CompetencyRadarChart from '../components/CompetencyRadarChart';
import ColdStartOnboarding from '../components/ColdStartOnboarding';
import { getStudentById, getStudentCompetencies, getStudentEnrollments, getCompetencies } from '../services/api';
import { formatCompetenciesForRadar, summarizeCompetencies, MAX_LEVEL } from '../utils/chartData';
import './Students.css';

export default function StudentProfile({ studentId, onBack }) {
    const [student, setStudent] = useState(null);
    const [radarData, setRadarData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [onboardingCompetencies, setOnboardingCompetencies] = useState(null);
    const [isProvisional, setIsProvisional] = useState(false);

    useEffect(() => {
        setLoading(true);
        setError(null);
        setOnboardingCompetencies(null);

        // The competency profile API is keyed by student number, so load the student first
        getStudentById(studentId)
            .then((studentResponse) => {
                setStudent(studentResponse.data);
                return Promise.all([
                    getStudentCompetencies(studentResponse.data.numeroEstudante),
                    getStudentEnrollments(studentId),
                ]);
            })
            .then(async ([competenciesResponse, enrollmentsResponse]) => {
                const competencies = competenciesResponse.data;
                const hasGrades = enrollmentsResponse.data.some((item) => item.grade != null);
                setRadarData(formatCompetenciesForRadar(competencies));
                setIsProvisional(competencies.some((item) => item.origem === 'AUTOAVALIACAO'));
                if (!hasGrades && competencies.length === 0) {
                    const catalogResponse = await getCompetencies();
                    setOnboardingCompetencies(catalogResponse.data);
                }
            })
            .catch((err) => {
                setError(err.response?.status === 404
                    ? 'Student not found.'
                    : 'Could not load the student profile. Please try again.');
            })
            .finally(() => setLoading(false));
    }, [studentId]);

    const refreshProfile = async () => {
        const response = await getStudentCompetencies(student.numeroEstudante);
        setRadarData(formatCompetenciesForRadar(response.data));
        setIsProvisional(true);
        setOnboardingCompetencies(null);
    };

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

                    {onboardingCompetencies?.length > 0 && (
                        <ColdStartOnboarding studentId={studentId} competencies={onboardingCompetencies}
                            onSaved={refreshProfile} />
                    )}
                    {isProvisional && <p className="profile-notice">Provisional profile based on self-assessment.</p>}

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
