import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import LoginButton from './LoginButton';

describe('LoginButton Component', () => {
    test('renders the Login button with correct text', () => {
        render(<LoginButton onClick={() => {}} />);
        const buttonElement = screen.getByRole('button', { name: /login/i });
        expect(buttonElement).toBeInTheDocument();
    });

    test('calls the onClick function when clicked', () => {
        const mockOnClick = jest.fn();
        render(<LoginButton onClick={mockOnClick} />);

        const buttonElement = screen.getByRole('button', { name: /login/i });
        fireEvent.click(buttonElement);

        expect(mockOnClick).toHaveBeenCalledTimes(1);
    });
});
