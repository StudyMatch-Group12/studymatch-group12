import React, { useEffect, useState } from 'react';
import {
    searchStudents,
    getGroupingContexts,
} from '../services/api';

export default function GroupingContexts({ onSelectContext }) {
    const [name, setName] = useState('');
    const [students, setStudents] = useState([]);
    const [selected, setSelected] = useState([]);
    const [contexts, setContexts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        let active = true;

        Promise.all([
            searchStudents({}),
            getGroupingContexts(),
        ])
            .then(([studentResponse, contextResponse]) => {
                if (!active) return;
                setStudents(studentResponse.data);
                setContexts(contextResponse.data);
            })
            .catch(() => {
                if (active) setError('Não foi possível carregar os dados.');
            })
            .finally(() => {
                if (active) setLoading(false);
            });

        return () => { active = false; };
    }, []);

    const toggleStudent = (id) => {
        setSelected(current => current.includes(id)
            ? current.filter(value => value !== id)
            : [...current, id]
        );
    };

    return (
        <main className="students-page">
            <h2>Contextos de agrupamento</h2>

            {loading && <p>A carregar…</p>}
            {error && <p role="alert">{error}</p>}

            {!loading && !error && (
                <>
                    <label htmlFor="context-name">Atividade</label>
                    <input
                        id="context-name"
                        value={name}
                        onChange={event =>
                            setName(event.target.value)
                        }
                    />

                    <fieldset>
                        <legend>Estudantes participantes</legend>
                        {students.map(student => (
                            <label
                                key={student.id}
                                style={{ display: 'block' }}
                            >
                                <input
                                    type="checkbox"
                                    checked={selected.includes(
                                        student.id
                                    )}
                                    onChange={() =>
                                        toggleStudent(student.id)
                                    }
                                />
                                {student.nome} (
                                {student.numeroEstudante})
                            </label>
                        ))}
                    </fieldset>

                    <h3>Contextos existentes</h3>
                    {contexts.length === 0 && (
                        <p>Ainda não existem contextos.</p>
                    )}
                    {contexts.map(context => (
                        <button
                            key={context.id}
                            type="button"
                            onClick={() =>
                                onSelectContext(context.id)
                            }
                        >
                            {context.name} — {
                                context.studentIds.length
                            } estudantes
                        </button>
                    ))}
                </>
            )}
        </main>
    );
}