import React, { useEffect, useState } from 'react';
import { generateGroups, getGroupGeneration, getGroupGenerations } from '../services/api';

export default function GroupResults({ contextId, onSelectStudent }) {
    const [generations, setGenerations] = useState([]);
    const [selected, setSelected] = useState(null);
    const [loading, setLoading] = useState(true);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        let active = true;
        setLoading(true);
        setSelected(null);
        setError('');
        getGroupGenerations(contextId)
            .then(({ data }) => {
                if (!active) return;
                setGenerations(data);
                setSelected(data[0] || null);
            })
            .catch(() => { if (active) setError('Não foi possível carregar as gerações.'); })
            .finally(() => { if (active) setLoading(false); });
        return () => { active = false; };
    }, [contextId]);

    const selectGeneration = async (id) => {
        setBusy(true);
        setError('');
        try {
            const { data } = await getGroupGeneration(id);
            if (data.contextId !== contextId) throw new Error('Contexto incorreto');
            setSelected(data);
        } catch {
            setError('Não foi possível consultar esta geração.');
        } finally {
            setBusy(false);
        }
    };

    const createGeneration = async () => {
        setBusy(true);
        setError('');
        try {
            const { data } = await generateGroups(contextId);
            setGenerations(current => [data, ...current]);
            setSelected(data);
        } catch (err) {
            setError(err.response?.data?.detail || 'Não foi possível gerar os grupos.');
        } finally {
            setBusy(false);
        }
    };

    return (
        <section aria-label="Resultados dos grupos">
            <h2>Grupos gerados</h2>
            {loading && <p>A carregar gerações...</p>}
            {error && <p role="alert">{error}</p>}
            {!loading && (
                <>
                    <button type="button" disabled={busy} onClick={createGeneration}>Gerar grupos</button>
                    {generations.length === 0 ? <p>Ainda não existem gerações para este contexto.</p> : (
                        <>
                            <h3>Gerações anteriores</h3>
                            <ul>{generations.map(generation => (
                                <li key={generation.id}>
                                    <button type="button" disabled={busy}
                                        aria-pressed={selected?.id === generation.id}
                                        onClick={() => selectGeneration(generation.id)}>
                                        Geração #{generation.id} — {new Date(generation.generatedAt).toLocaleString('pt-PT')}
                                    </button>
                                </li>
                            ))}</ul>
                        </>
                    )}
                    {selected && (
                        <div>
                            <h3>{selected.contextName} — geração #{selected.id}</h3>
                            {selected.groups.length === 0 ? <p>Esta geração não contém grupos.</p> :
                                selected.groups.map(group => (
                                    <section key={group.number}>
                                        <h4>Grupo {group.number}</h4>
                                        <ul>{group.members.map(member => (
                                            <li key={member.id}>
                                                <button type="button" onClick={() => onSelectStudent(member.id)}>
                                                    {member.name} ({member.studentNumber})
                                                </button> — {member.course || 'Curso não indicado'}
                                            </li>
                                        ))}</ul>
                                    </section>
                                ))}
                        </div>
                    )}
                </>
            )}
        </section>
    );
}
