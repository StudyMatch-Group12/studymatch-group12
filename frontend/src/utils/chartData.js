// Competency levels returned by GET /api/competencias/estudantes/{numeroEstudante}
// range from 1 to 5 (see CompetenciaService.converterParaNivel)
export const MIN_LEVEL = 0;
export const MAX_LEVEL = 5;

// A radar chart needs at least three axes to form a readable shape
export const MIN_RADAR_AXES = 3;

const clampLevel = (level) => Math.min(MAX_LEVEL, Math.max(MIN_LEVEL, Math.round(level)));

// Converts the competency profile API response ([{ nome, descricao, nivel, origem }])
// into the data format expected by the Recharts RadarChart. Entries without a name
// or with a non-numeric level are ignored, levels are clamped to 0-5 and duplicated
// competencies keep the highest level.
export function formatCompetenciesForRadar(competencies) {
    if (!Array.isArray(competencies)) return [];

    const pointsByName = new Map();
    competencies.forEach((item) => {
        const name = typeof item?.nome === 'string' ? item.nome.trim() : '';
        const level = Number(item?.nivel);
        if (name === '' || item?.nivel === null || Number.isNaN(level)) return;

        const current = pointsByName.get(name);
        const clamped = clampLevel(level);
        if (current === undefined || clamped > current.level) {
            pointsByName.set(name, {
                competency: name,
                description: item.descricao || '',
                level: clamped,
                fullMark: MAX_LEVEL,
            });
        }
    });

    return Array.from(pointsByName.values())
        .sort((a, b) => a.competency.localeCompare(b.competency));
}

// Summary shown next to the chart: average level, strongest and weakest competency
export function summarizeCompetencies(radarData) {
    if (!Array.isArray(radarData) || radarData.length === 0) {
        return { average: 0, strongest: null, weakest: null };
    }

    const total = radarData.reduce((sum, item) => sum + item.level, 0);
    const strongest = radarData.reduce((best, item) => (item.level > best.level ? item : best));
    const weakest = radarData.reduce((worst, item) => (item.level < worst.level ? item : worst));

    return {
        average: Math.round((total / radarData.length) * 10) / 10,
        strongest: strongest.competency,
        weakest: weakest.competency,
    };
}
