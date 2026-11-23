import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import GroupingContexts from './GroupingContexts';
import { searchStudents, getGroupingContexts, createGroupingContext } from '../services/api';

jest.mock('../services/api', () => ({
    searchStudents: jest.fn(),
    getGroupingContexts: jest.fn(),
    createGroupingContext: jest.fn(),
}));

beforeEach(() => {
    searchStudents.mockResolvedValue({
        data: [{ id: 1, nome: 'Ana', numeroEstudante: '001' }],
    });
    getGroupingContexts.mockResolvedValue({ data: [] });
});

afterEach(() => jest.clearAllMocks());

test('creates context with selected student and opens it', async () => {
    createGroupingContext.mockResolvedValue({
        data: { id: 9, name: 'Projeto', studentIds: [1] },
    });
    const select = jest.fn();

    render(<GroupingContexts onSelectContext={select} />);
    fireEvent.change(await screen.findByLabelText('Atividade'), {
        target: { value: 'Projeto' },
    });
    fireEvent.click(screen.getByRole('checkbox'));
    fireEvent.click(screen.getByRole('button', { name: 'Criar contexto' }));

    await waitFor(() =>
        expect(createGroupingContext).toHaveBeenCalledWith({
            name: 'Projeto',
            studentIds: [1],
        })
    );
    expect(select).toHaveBeenCalledWith(9);
});