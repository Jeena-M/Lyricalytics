import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import CreateButton from './CreateButton';

describe('CreateButton Component', () => {
    test('renders the Create button with correct text', () => {
        render(<CreateButton onClick={() => {}} />);
        const buttonElement = screen.getByRole('button', { name: /create/i });
        expect(buttonElement).toBeInTheDocument();
    });

    test('calls the onClick function when clicked', () => {
        const mockOnClick = jest.fn();
        render(<CreateButton onClick={mockOnClick} />);

        const buttonElement = screen.getByRole('button', { name: /create/i });
        fireEvent.click(buttonElement);

        expect(mockOnClick).toHaveBeenCalledTimes(1);
    });
});
