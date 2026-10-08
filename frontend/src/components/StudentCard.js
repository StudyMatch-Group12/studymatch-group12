import React from 'react';

export default function StudentCard({ student }) {
    if (!student) return <p>Nenhum estudante selecionado.</p>;

    return (
        <div style={{ border: '1px solid #ccc', borderRadius: '8px', padding: '1rem', margin: '1rem 0' }}>
            <h3>{student.name || 'Estudante sem nome'}</h3>
            <p><strong>Número de Estudante:</strong> {student.number || 'N/A'}</p>
            <p><strong>Curso:</strong> {student.degree || 'N/A'}</p>
        </div>
    );
}