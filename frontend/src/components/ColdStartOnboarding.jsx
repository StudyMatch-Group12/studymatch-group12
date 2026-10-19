import React, { useState } from 'react';
import { createColdStartProfile } from '../services/api';

export default function ColdStartOnboarding({ studentId, competencies, onSaved }) {
    const [levels, setLevels] = useState({});
    const [interests, setInterests] = useState('');
    const [role, setRole] = useState('');
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    const submit = async (event, neutral = false) => {
        event.preventDefault();
        setSaving(true);
        setError('');
        const niveis = Object.fromEntries(competencies.map((item) => [item.id, neutral ? 3 : Number(levels[item.id] || 3)]));
        try {
            await createColdStartProfile(studentId, {
                interesses: neutral ? '' : interests,
                preferenciaPapel: neutral ? '' : role,
                niveis,
            });
            await onSaved();
        } catch (err) {
            setError(typeof err.response?.data === 'string'
                ? err.response.data : 'Could not save your provisional profile. Please try again.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <section className="profile-section cold-start-onboarding">
            <h3>Start your profile</h3>
            <p>You have no academic grades yet. Rate your skills from 1 to 5 to create a provisional profile.</p>
            <form onSubmit={submit}>
                {competencies.map((item) => (
                    <label key={item.id}>
                        {item.nome}
                        <select value={levels[item.id] || 3}
                            onChange={(event) => setLevels({ ...levels, [item.id]: Number(event.target.value) })}>
                            {[1, 2, 3, 4, 5].map((level) => <option key={level} value={level}>{level}</option>)}
                        </select>
                    </label>
                ))}
                <label>
                    Interests
                    <input maxLength="255" value={interests} onChange={(event) => setInterests(event.target.value)} />
                </label>
                <label>
                    Preferred role in a team
                    <input maxLength="255" value={role} onChange={(event) => setRole(event.target.value)} />
                </label>
                <div className="cold-start-actions">
                    <button type="submit" disabled={saving}>Save provisional profile</button>
                    <button type="button" disabled={saving} onClick={(event) => submit(event, true)}>
                        Skip and use neutral levels
                    </button>
                </div>
            </form>
            {error && <p role="alert" className="error-message">{error}</p>}
        </section>
    );
}
