import React from 'react';
import StudentCard from '../components/StudentCard';

export default function StudentProfile() {
    const dummyStudent = {
        name: 'Estudante Exemplo',
        number: 'a12345',
        degree: 'Engenharia Informática',
    };

    return (
        <main style={{ padding: '2rem' }}>
            <h2>Perfil do Estudante</h2>
            <StudentCard student={dummyStudent} />
            <section style={{ marginTop: '2rem' }}>
                <h3>Estratégia Cold-Start & Competências</h3>
                <p>A carregar perfil inicial ou dados históricos do estudante...</p>
            </section>
        </main>
    );
}