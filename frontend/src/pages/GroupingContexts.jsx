import React, { useEffect, useState } from 'react';
import GroupingCredentials from '../components/GroupingCredentials';

import {
    searchStudents,
    getGroupingContexts,
    createGroupingContext,
} from '../services/api';

export default function GroupingContexts({ onSelectContext }) {
    const [name, setName] = useState('');
    const [students, setStudents] = useState([]);
    const [selected, setSelected] = useState([]);
    const [contexts, setContexts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [loadError, setLoadError] = useState('');
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
                if (active) setLoadError('Não foi possível carregar os dados.');
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

    const submit = async (event) => {
        event.preventDefault();

        if (!name.trim() || selected.length === 0) {
            setError('Indica um nome e seleciona estudantes.');
            return;
        }

        setSaving(true);
        setError('');

        try {
            const response = await createGroupingContext({
                name: name.trim(),
                studentIds: selected,
            });
            setContexts(current => [...current, response.data]);
            setName('');
            setSelected([]);
            onSelectContext(response.data.id);
        } catch (err) {
            setError(
                err.response?.data?.detail ||
                'Não foi possível criar o contexto.'
            );
        } finally {
            setSaving(false);
        }
    };

    return (
        <main className="students-page">
            <h2>Contextos de agrupamento</h2>
            <GroupingCredentials />
            {loading && <p>A carregar…</p>}
            {(loadError || error) && <p role="alert">{loadError || error}</p>}

            {!loading && !loadError && (
                <>
                    <form onSubmit={submit}>
                        <label htmlFor="context-name">Atividade</label>
                        <input
                            id="context-name"
                            disabled={saving}
                            value={name}
                            onChange={event =>
                                setName(event.target.value)
                            }
                        />

                        <fieldset disabled={saving}>
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

                        <button type="submit" disabled={saving}>
                            {saving
                                ? 'A guardar…'
                                : 'Criar contexto'}
                        </button>
                    </form>

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