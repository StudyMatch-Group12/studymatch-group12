import { formatCompetenciesForRadar, summarizeCompetencies, MAX_LEVEL } from './chartData';

describe('formatCompetenciesForRadar', () => {
    test('maps API data to radar points sorted by competency name', () => {
        const apiData = [
            { competency: 'Teamwork', level: 70, source: 'SELF_ASSESSMENT' },
            { competency: 'Databases', level: 40, source: 'ACADEMIC_HISTORY' },
        ];

        expect(formatCompetenciesForRadar(apiData)).toEqual([
            { competency: 'Databases', level: 40, fullMark: MAX_LEVEL },
            { competency: 'Teamwork', level: 70, fullMark: MAX_LEVEL },
        ]);
    });

    test('clamps levels to the 0-100 range and rounds decimals', () => {
        const apiData = [
            { competency: 'A', level: 150 },
            { competency: 'B', level: -10 },
            { competency: 'C', level: 49.6 },
        ];

        expect(formatCompetenciesForRadar(apiData).map((point) => point.level)).toEqual([100, 0, 50]);
    });

    test('ignores entries without a name or with an invalid level', () => {
        const apiData = [
            { competency: '', level: 50 },
            { competency: 'Valid', level: 60 },
            { competency: 'NoLevel', level: null },
            { competency: 'Text', level: 'abc' },
            null,
        ];

        expect(formatCompetenciesForRadar(apiData)).toEqual([
            { competency: 'Valid', level: 60, fullMark: MAX_LEVEL },
        ]);
    });

    test('keeps the highest level when a competency is repeated', () => {
        const apiData = [
            { competency: 'Programming', level: 30 },
            { competency: 'Programming', level: 80 },
        ];

        expect(formatCompetenciesForRadar(apiData)).toEqual([
            { competency: 'Programming', level: 80, fullMark: MAX_LEVEL },
        ]);
    });

    test('returns an empty array for missing or invalid input', () => {
        expect(formatCompetenciesForRadar(undefined)).toEqual([]);
        expect(formatCompetenciesForRadar({})).toEqual([]);
        expect(formatCompetenciesForRadar([])).toEqual([]);
    });
});

describe('summarizeCompetencies', () => {
    test('calculates average, strongest and weakest competency', () => {
        const radarData = [
            { competency: 'Databases', level: 40, fullMark: 100 },
            { competency: 'Programming', level: 90, fullMark: 100 },
            { competency: 'Teamwork', level: 65, fullMark: 100 },
        ];

        expect(summarizeCompetencies(radarData)).toEqual({
            average: 65,
            strongest: 'Programming',
            weakest: 'Databases',
        });
    });

    test('returns an empty summary when there is no data', () => {
        expect(summarizeCompetencies([])).toEqual({ average: 0, strongest: null, weakest: null });
    });
});
