import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import CancelButton from './CancelButton';

describe('CancelButton Component', () => {
    test('renders the Cancel button with correct text', () => {
        render(<CancelButton onClick={() => {}} />);
        const buttonElement = screen.getByRole('button', { name: /cancel/i });
        expect(buttonElement).toBeInTheDocument();
    });

    test('calls the onClick function when clicked', () => {
        const mockOnClick = jest.fn();
        render(<CancelButton onClick={mockOnClick} />);

        const buttonElement = screen.getByRole('button', { name: /cancel/i });
        fireEvent.click(buttonElement);

        expect(mockOnClick).toHaveBeenCalledTimes(1);
    });
});
