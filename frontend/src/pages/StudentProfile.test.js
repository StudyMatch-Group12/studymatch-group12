import { render, screen } from '@testing-library/react';
import StudentProfile from './StudentProfile';
import { getStudentById, getStudentCompetencies } from '../services/api';

jest.mock('../services/api', () => ({
    getStudentById: jest.fn(),
    getStudentCompetencies: jest.fn(),
}));

describe('StudentProfile', () => {
    afterEach(() => jest.clearAllMocks());

    test('loads competencies by student number and shows the summary', async () => {
        getStudentById.mockResolvedValue({
            data: { id: 7, nome: 'Ana Costa', numeroEstudante: '3000101', curso: 'Gestão' },
        });
        getStudentCompetencies.mockResolvedValue({
            data: [
                { nome: 'Programming', descricao: '', nivel: 5, origem: 'HISTORICO_ACADEMICO' },
                { nome: 'Databases', descricao: '', nivel: 2, origem: 'HISTORICO_ACADEMICO' },
            ],
        });

        render(<StudentProfile studentId={7} />);

        expect(await screen.findByText('Ana Costa')).toBeInTheDocument();
        expect(getStudentById).toHaveBeenCalledWith(7);
        expect(getStudentCompetencies).toHaveBeenCalledWith('3000101');
        expect(screen.getByText('3.5 / 5')).toBeInTheDocument();
    });

    test('shows an empty state when the student has no competency profile yet', async () => {
        getStudentById.mockResolvedValue({
            data: { id: 8, nome: 'Bruno Silva', numeroEstudante: '3000202', curso: 'Direito' },
        });
        getStudentCompetencies.mockResolvedValue({ data: [] });

        render(<StudentProfile studentId={8} />);

        expect(await screen.findByText('This student has no competency data yet.')).toBeInTheDocument();
    });

    test('shows an error when the student does not exist', async () => {
        getStudentById.mockRejectedValue({ response: { status: 404 } });

        render(<StudentProfile studentId={99} />);

        expect(await screen.findByText('Student not found.')).toBeInTheDocument();
        expect(getStudentCompetencies).not.toHaveBeenCalled();
    });
});
