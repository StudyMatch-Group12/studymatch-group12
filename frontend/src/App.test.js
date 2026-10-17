import { render, screen } from '@testing-library/react';
import App from './App';

test('renders the navigation and the registration page by default', () => {
  render(<App />);
  expect(screen.getByRole('button', { name: 'Register' })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Students' })).toBeInTheDocument();
  expect(screen.getByText('Criar Conta de Estudante')).toBeInTheDocument();
});
