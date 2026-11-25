import React, { useEffect, useState } from 'react';
import { generateGroups, getGroupGeneration, getGroupGenerations, getStudentCompetencies } from '../services/api';
import {
    generateGroups,
    getGroupGeneration,
    getGroupGenerations,
    getStudentCompetencies,
    getCompetencies,
} from '../services/api';

function GroupMember({ member, onSelectStudent }) {
    const [competencies, setCompetencies] = useState(null);

    useEffect(() => {
        let active = true;
        setCompetencies(null);
        getStudentCompetencies(member.studentNumber)
            .then(({ data }) => { if (active) setCompetencies(data); })
            .catch(() => { if (active) setCompetencies([]); });
        return () => { active = false; };
    }, [member.studentNumber]);

    const strongest = competencies?.slice().sort((a, b) => b.nivel - a.nivel).slice(0, 2);
    return (
        <li>
            <button type="button" onClick={() => onSelectStudent(member.id)}>
                {member.name} ({member.studentNumber})
            </button> — {member.course || 'Curso não indicado'}
            {strongest?.length > 0 && <span> · Competências: {strongest.map(item => `${item.nome} (${item.nivel}/5)`).join(', ')}</span>}
            {competencies?.length === 0 && <span> · Sem competências registadas</span>}
        </li>
    );
}

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

    useEffect(() => {
        let active = true;

        getCompetencies()
            .then(({ data }) => {
                if (active) {
                    setCompetencyNames(
                        Object.fromEntries(data.map(item => [item.id, item.nome]))
                    );
                }
            })
            .catch(() => {});

        return () => { active = false; };
    }, []);

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

    const [competencyNames, setCompetencyNames] = useState({});

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
                                        {group.meanScore != null && (
                                            <p>
                                                Média: {group.meanScore.toFixed(2)}/5;
                                                diferença entre pontuações: {group.scoreSpread.toFixed(2)}.
                                            </p>
                                        )}
                                        <ul>{group.members.map(member => (
                                            <GroupMember key={member.id} member={member} onSelectStudent={onSelectStudent} />
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
