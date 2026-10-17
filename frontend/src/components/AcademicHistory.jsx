import React, { useState, useEffect } from 'react';
import { getCourseUnits, getStudentEnrollments, registerEnrollment } from '../services/api';

export default function AcademicHistory({ studentId }) {
    const [courseUnits, setCourseUnits] = useState([]);
    const [history, setHistory] = useState([]);
    const [selectedUnit, setSelectedUnit] = useState('');
    const [academicYear, setAcademicYear] = useState('2025/2026');
    const [grade, setGrade] = useState('');
    const [message, setMessage] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadCourseUnits();
        if (studentId) {
            loadHistory();
        }
    }, [studentId]);

    const loadCourseUnits = async () => {
        try {
            const response = await getCourseUnits();
            setCourseUnits(response.data);
        } catch (err) {
            console.error('Erro ao carregar UCs:', err);
        }
    };

    const loadHistory = async () => {
        try {
            const response = await getStudentEnrollments(studentId);
            setHistory(response.data);
        } catch (err) {
            console.error('Erro ao carregar histórico:', err);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        setError(null);

        const gradeNum = parseFloat(grade);
        if (isNaN(gradeNum) || gradeNum < 0 || gradeNum > 20) {
            setError('A nota deve estar entre 0 e 20 valores.');
            return;
        }

        try {
            await registerEnrollment(studentId, {
                courseUnitId: parseInt(selectedUnit, 10),
                academicYear,
                grade: gradeNum,
            });
            setMessage('Registo de histórico adicionado com sucesso!');
            setGrade('');
            loadHistory();
        } catch (err) {
            const errorMsg = err.response?.data || err.message || 'Erro ao registar histórico';
            setError(errorMsg);
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
            <h3>Registo de Histórico Académico</h3>

            {message && <div style={{ color: 'green', marginBottom: '10px' }}>{message}</div>}
            {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                <div>
                    <label>Unidade Curricular: </label>
                    <select
                        value={selectedUnit}
                        onChange={(e) => setSelectedUnit(e.target.value)}
                        required
                        style={{ width: '100%', padding: '8px' }}
                    >
                        <option value="">Selecione uma UC...</option>
                        {courseUnits.map((cu) => (
                            <option key={cu.id} value={cu.id}>
                                {cu.code} - {cu.name} ({cu.ects} ECTS)
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label>Ano Letivo: </label>
                    <input
                        type="text"
                        value={academicYear}
                        onChange={(e) => setAcademicYear(e.target.value)}
                        required
                        placeholder="Ex: 2025/2026"
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>

                <div>
                    <label>Classificação (0 a 20): </label>
                    <input
                        type="number"
                        step="0.1"
                        min="0"
                        max="20"
                        value={grade}
                        onChange={(e) => setGrade(e.target.value)}
                        required
                        placeholder="Ex: 16.5"
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>

                <button type="submit" style={{ padding: '10px', background: '#0066cc', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    Registar Unidade Curricular
                </button>
            </form>

            <hr style={{ margin: '24px 0' }} />

            <h4>Histórico Concluído</h4>
            {history.length === 0 ? (
                <p>Ainda não existem registos de histórico para este estudante.</p>
            ) : (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                    <tr style={{ borderBottom: '1px solid #ccc', textAlign: 'left' }}>
                        <th>UC</th>
                        <th>Ano Letivo</th>
                        <th>Nota</th>
                    </tr>
                    </thead>
                    <tbody>
                    {history.map((item) => (
                        <tr key={item.id} style={{ borderBottom: '1px solid #eee' }}>
                            <td>{item.courseUnit?.name || item.courseUnit?.code}</td>
                            <td>{item.academicYear}</td>
                            <td><strong>{item.grade}</strong></td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}