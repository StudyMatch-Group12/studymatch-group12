import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import GroupResults from './GroupResults';
import {
    generateGroups,
    getGroupGeneration,
    getGroupGenerations,
    getStudentCompetencies,
    getCompetencies,
    exportGroupGeneration,
} from '../services/api';

jest.mock('../services/api', () => ({
    generateGroups: jest.fn(),
    getGroupGeneration: jest.fn(),
    getGroupGenerations: jest.fn(),
    getStudentCompetencies: jest.fn(),
    getCompetencies: jest.fn(),
    exportGroupGeneration: jest.fn(),
}));

const generation = {
    id: 8,
    contextId: 2,
    contextName: 'Projeto',
    generatedAt: '2026-11-23T10:00:00',
    groups: [{
        number: 1,
        meanScore: 3.5,
        scoreSpread: 1,
        complementaryCompetencyIds: [1],
        members: [
            {
                id: 4,
                name: 'Ana',
                studentNumber: '123',
                course: 'Informática',
            },
        ],
    }],
};

beforeEach(() => {
    getStudentCompetencies.mockResolvedValue({
        data: [{ nome: 'Programação', nivel: 5 }],
    });
    getCompetencies.mockResolvedValue({
        data: [{ id: 1, nome: 'Programação' }],
    });
});

afterEach(() => {
    jest.clearAllMocks();
});

test('shows persisted groups and opens a student profile', async () => {
    getGroupGenerations.mockResolvedValue({ data: [generation] });
    const onSelectStudent = jest.fn();

    render(
        <GroupResults
            contextId={2}
            onSelectStudent={onSelectStudent}
        />
    );

    expect(await screen.findByText('Grupo 1')).toBeInTheDocument();
    expect(await screen.findByText(/Programação \(5\/5\)/))
        .toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: 'Ana (123)' }));
    expect(onSelectStudent).toHaveBeenCalledWith(4);
});

test('shows an empty state and can generate groups', async () => {
    getGroupGenerations.mockResolvedValue({ data: [] });
    generateGroups.mockResolvedValue({ data: generation });

    render(
        <GroupResults contextId={2} onSelectStudent={() => {}} />
    );

    expect(
        await screen.findByText(
            'Ainda não existem gerações para este contexto.'
        )
    ).toBeInTheDocument();

    fireEvent.click(
        screen.getByRole('button', { name: 'Gerar grupos' })
    );

    expect(await screen.findByText('Grupo 1')).toBeInTheDocument();
    expect(generateGroups).toHaveBeenCalledWith(2);
});

test('reports loading errors', async () => {
    getGroupGenerations.mockRejectedValue(new Error('offline'));

    render(
        <GroupResults contextId={2} onSelectStudent={() => {}} />
    );

    expect(await screen.findByRole('alert'))
        .toHaveTextContent('Não foi possível carregar as gerações.');
});

test('loads an older generation on selection', async () => {
    getGroupGenerations.mockResolvedValue({
        data: [generation, { ...generation, id: 7 }],
    });
    getGroupGeneration.mockResolvedValue({
        data: { ...generation, id: 7, groups: [] },
    });

    render(
        <GroupResults contextId={2} onSelectStudent={() => {}} />
    );

    fireEvent.click(
        await screen.findByRole('button', { name: /Geração #7/ })
    );

    expect(
        await screen.findByText('Esta geração não contém grupos.')
    ).toBeInTheDocument();
    expect(getGroupGeneration).toHaveBeenCalledWith(7);
});

test('explains groups and preserves history after another generation', async () => {
    getGroupGenerations.mockResolvedValue({ data: [generation] });
    generateGroups.mockResolvedValue({
        data: { ...generation, id: 9 },
    });

    render(
        <GroupResults contextId={2} onSelectStudent={() => {}} />
    );

    expect(await screen.findByText(/Média: 3.50\/5/))
        .toHaveTextContent('Programação');

    fireEvent.click(
        screen.getByRole('button', { name: 'Gerar grupos' })
    );

    expect(
        await screen.findByRole('button', { name: /Geração #9/ })
    ).toBeInTheDocument();
    expect(
        screen.getByRole('button', { name: /Geração #8/ })
    ).toBeInTheDocument();
});

test('reports unauthorized generation and allows retry', async () => {
    getGroupGenerations.mockResolvedValue({ data: [] });
    generateGroups
        .mockRejectedValueOnce({ response: { status: 401 } })
        .mockResolvedValueOnce({ data: generation });

    render(
        <GroupResults contextId={2} onSelectStudent={() => {}} />
    );

    fireEvent.click(
        await screen.findByRole('button', { name: 'Gerar grupos' })
    );

    expect(await screen.findByRole('alert'))
        .toHaveTextContent('Credenciais inválidas');

    fireEvent.click(
        screen.getByRole('button', { name: 'Gerar grupos' })
    );

    expect(await screen.findByText('Grupo 1')).toBeInTheDocument();
});

test('exports the selected generation', async () => {
    getGroupGenerations.mockResolvedValue({ data: [generation] });
    exportGroupGeneration.mockResolvedValue({
        data: new Blob(['csv']),
    });

    URL.createObjectURL = jest.fn(() => 'blob:test');
    URL.revokeObjectURL = jest.fn();

    const click = jest
        .spyOn(HTMLAnchorElement.prototype, 'click')
        .mockImplementation(() => {});

    try {
        render(
            <GroupResults contextId={2} onSelectStudent={() => {}} />
        );

        fireEvent.click(
            await screen.findByRole('button', { name: 'Exportar CSV' })
        );

        await waitFor(() => {
            expect(exportGroupGeneration).toHaveBeenCalledWith(8);
            expect(click).toHaveBeenCalled();
            expect(URL.revokeObjectURL)
                .toHaveBeenCalledWith('blob:test');
        });
    } finally {
        click.mockRestore();
    }
});