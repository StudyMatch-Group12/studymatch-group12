export const MIN_LEVEL = 0;
export const MAX_LEVEL = 100;

// A radar chart needs at least three axes to form a readable shape
export const MIN_RADAR_AXES = 3;

const clampLevel = (level) => Math.min(MAX_LEVEL, Math.max(MIN_LEVEL, Math.round(level)));

// Converts the API response ([{ competency, level, source }]) into the data
// format expected by the Recharts RadarChart. Entries without a name or with a
// non-numeric level are ignored, levels are clamped to 0-100 and duplicated
// competencies keep the highest level.
export function formatCompetenciesForRadar(competencies) {
    if (!Array.isArray(competencies)) return [];

    const levelsByName = new Map();
    competencies.forEach((item) => {
        const name = typeof item?.competency === 'string' ? item.competency.trim() : '';
        const level = Number(item?.level);
        if (name === '' || item?.level === null || Number.isNaN(level)) return;

        const current = levelsByName.get(name);
        const clamped = clampLevel(level);
        if (current === undefined || clamped > current) {
            levelsByName.set(name, clamped);
        }
    });

    return Array.from(levelsByName, ([competency, level]) => ({
        competency,
        level,
        fullMark: MAX_LEVEL,
    })).sort((a, b) => a.competency.localeCompare(b.competency));
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
        average: Math.round(total / radarData.length),
        strongest: strongest.competency,
        weakest: weakest.competency,
    };
}
