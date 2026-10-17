import React, { useEffect, useState } from 'react';
import StudentCard from '../components/StudentCard';
import { getStatus } from '../services/api';

export default function StudentProfile() {
    const [backendStatus, setBackendStatus] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
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
                <h3>Estratégia Cold-Start & Competências</h3>
                <p>
                    A carregar perfil inicial ou dados históricos do estudante...
                </p>
            </section>
        </main>
    );
}