import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import GroupResults from './GroupResults';
import { generateGroups, getGroupGeneration, getGroupGenerations } from '../services/api';

jest.mock('../services/api', () => ({
    generateGroups: jest.fn(),
    getGroupGeneration: jest.fn(),
    getGroupGenerations: jest.fn(),
}));

const generation = {
    id: 8, contextId: 2, contextName: 'Projeto', generatedAt: '2026-11-23T10:00:00',
    groups: [{ number: 1, members: [
        { id: 4, name: 'Ana', studentNumber: '123', course: 'Informática' },
    ] }],
};

afterEach(() => jest.clearAllMocks());

test('shows persisted groups and opens a student profile', async () => {
    getGroupGenerations.mockResolvedValue({ data: [generation] });
    const onSelectStudent = jest.fn();
    render(<GroupResults contextId={2} onSelectStudent={onSelectStudent} />);
    expect(await screen.findByText('Grupo 1')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Ana (123)' }));
    expect(onSelectStudent).toHaveBeenCalledWith(4);
});

test('shows an empty state and can generate groups', async () => {
    getGroupGenerations.mockResolvedValue({ data: [] });
    generateGroups.mockResolvedValue({ data: generation });
    render(<GroupResults contextId={2} onSelectStudent={() => {}} />);
    expect(await screen.findByText('Ainda não existem gerações para este contexto.')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Gerar grupos' }));
    expect(await screen.findByText('Grupo 1')).toBeInTheDocument();
    expect(generateGroups).toHaveBeenCalledWith(2);
});

test('reports loading errors', async () => {
    getGroupGenerations.mockRejectedValue(new Error('offline'));
    render(<GroupResults contextId={2} onSelectStudent={() => {}} />);
    expect(await screen.findByRole('alert')).toHaveTextContent('Não foi possível carregar as gerações.');
});

test('loads an older generation on selection', async () => {
    getGroupGenerations.mockResolvedValue({ data: [generation, { ...generation, id: 7 }] });
    getGroupGeneration.mockResolvedValue({ data: { ...generation, id: 7, groups: [] } });
    render(<GroupResults contextId={2} onSelectStudent={() => {}} />);
    fireEvent.click(await screen.findByRole('button', { name: /Geração #7/ }));
    await waitFor(() => expect(screen.getByText('Esta geração não contém grupos.')).toBeInTheDocument());
    expect(getGroupGeneration).toHaveBeenCalledWith(7);
});
