import { formatCompetenciesForRadar, summarizeCompetencies, MAX_LEVEL } from './chartData';

describe('formatCompetenciesForRadar', () => {
    test('maps the competency profile API response to radar points sorted by name', () => {
        const apiData = [
            { nome: 'Teamwork', descricao: 'Works well in groups', nivel: 4, origem: 'HISTORICO_ACADEMICO' },
            { nome: 'Databases', descricao: 'SQL and modelling', nivel: 2, origem: 'HISTORICO_ACADEMICO' },
        ];

        expect(formatCompetenciesForRadar(apiData)).toEqual([
            { competency: 'Databases', description: 'SQL and modelling', level: 2, fullMark: MAX_LEVEL },
            { competency: 'Teamwork', description: 'Works well in groups', level: 4, fullMark: MAX_LEVEL },
        ]);
    });

    test('clamps levels to the 0-5 range and rounds decimals', () => {
        const apiData = [
            { nome: 'A', nivel: 9 },
            { nome: 'B', nivel: -1 },
            { nome: 'C', nivel: 3.6 },
        ];

        expect(formatCompetenciesForRadar(apiData).map((point) => point.level)).toEqual([5, 0, 4]);
    });

    test('ignores entries without a name or with an invalid level', () => {
        const apiData = [
            { nome: '', nivel: 3 },
            { nome: 'Valid', nivel: 3 },
            { nome: 'NoLevel', nivel: null },
            { nome: 'Text', nivel: 'abc' },
            null,
        ];

        expect(formatCompetenciesForRadar(apiData)).toEqual([
            { competency: 'Valid', description: '', level: 3, fullMark: MAX_LEVEL },
        ]);
    });

    test('keeps the highest level when a competency is repeated', () => {
        const apiData = [
            { nome: 'Programming', nivel: 2 },
            { nome: 'Programming', nivel: 5 },
        ];

        expect(formatCompetenciesForRadar(apiData).map((point) => point.level)).toEqual([5]);
    });

    test('returns an empty array for missing or invalid input', () => {
        expect(formatCompetenciesForRadar(undefined)).toEqual([]);
        expect(formatCompetenciesForRadar({})).toEqual([]);
        expect(formatCompetenciesForRadar([])).toEqual([]);
    });
});

describe('summarizeCompetencies', () => {
    test('calculates average (one decimal), strongest and weakest competency', () => {
        const radarData = [
            { competency: 'Databases', level: 2, fullMark: MAX_LEVEL },
            { competency: 'Programming', level: 5, fullMark: MAX_LEVEL },
            { competency: 'Teamwork', level: 4, fullMark: MAX_LEVEL },
        ];

        expect(summarizeCompetencies(radarData)).toEqual({
            average: 3.7,
            strongest: 'Programming',
            weakest: 'Databases',
        });
    });

    test('returns an empty summary when there is no data', () => {
        expect(summarizeCompetencies([])).toEqual({ average: 0, strongest: null, weakest: null });
    });
});
