import React, { useEffect, useState } from 'react';
import { getGroupingContexts } from '../services/api';

export default function GroupingContexts({ onSelectContext }) {
    const [contexts, setContexts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        let active = true;

        getGroupingContexts()
            .then(response => {
                if (active) setContexts(response.data);
            })
            .catch(() => {
                if (active) setError('Não foi possível carregar os contextos.');
            })
            .finally(() => {
                if (active) setLoading(false);
            });

        return () => { active = false; };
    }, []);

    return (
        <main className="students-page">
            <h2>Contextos de agrupamento</h2>

            {loading && <p>A carregar…</p>}
            {error && <p role="alert">{error}</p>}

            {!loading && !error && (
                <>
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