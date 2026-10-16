import React, { useEffect, useState } from 'react';
import StudentCard from '../components/StudentCard';
import { getStatus, getCompetencyProfile } from '../services/api';

export default function StudentProfile() {
    const [backendStatus, setBackendStatus] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [competencias, setCompetencias] = useState([]);
    const [competenciasLoading, setCompetenciasLoading] = useState(true);
    const [competenciasError, setCompetenciasError] = useState(null);
    const dummyStudent = {
        name: 'Estudante Exemplo',
        number: 'a12345',
        degree: 'Engenharia Informática',
    };
     
    useEffect(() => {
        getStatus()
            .then((response) => {
                setBackendStatus(response.data);
                setError(null);
            })
            .catch(() => {
                setError('Não foi possível comunicar com o backend.');
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    useEffect(() => {
        getCompetencyProfile('a12345')
            .then((response) => {
                setCompetencias(response.data);
                setCompetenciasError(null);
            })
            .catch(() => {
                setCompetenciasError(
                    'Não foi possível carregar o perfil de competências.'
                );
            })
            .finally(() => {
                setCompetenciasLoading(false);
            });
    }, []);

     return (
        <main style={{ padding: '2rem' }}>
            <h2>Perfil do Estudante</h2>

            <StudentCard student={dummyStudent} />

            <section style={{ marginTop: '2rem' }}>
                <h3>Estado do Backend</h3>

                {loading && <p>A verificar ligação...</p>}

                {error && <p>{error}</p>}

                {backendStatus && (
                    <pre>
                        {JSON.stringify(backendStatus, null, 2)}
                    </pre>
                )}
            </section>

            <section style={{ marginTop: '2rem' }}>
                <h3>Perfil de Competências</h3>

                {competenciasLoading && (
                    <p>A carregar competências...</p>
                )}

                {competenciasError && (
                    <p>{competenciasError}</p>
                )}

                {!competenciasLoading &&
                    !competenciasError &&
                    competencias.length === 0 && (
                        <p>
                            Ainda não existem competências calculadas para este estudante.
                        </p>
                    )}

                {competencias.length > 0 && (
                    <ul>
                        {competencias.map((competencia, index) => (
                            <li key={index}>
                                <strong>{competencia.nome}</strong>
                                {' — '}
                                Nível {competencia.nivel}
                                {competencia.descricao &&
                                    ` — ${competencia.descricao}`}
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </main>
    );
}