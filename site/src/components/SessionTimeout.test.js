import React from 'react';
import { render, act } from '@testing-library/react';
import SessionTimeout from './SessionTimeout';
import { useNavigate } from 'react-router-dom';

jest.mock('react-router-dom', () => ({
    useNavigate: jest.fn(),
}));

describe('SessionTimeout Component', () => {
    let logoutMock;
    let navigateMock;

    beforeAll(() => {
        jest.useFakeTimers();
    });

    beforeEach(() => {
        logoutMock = jest.fn();
        navigateMock = jest.fn();
        useNavigate.mockReturnValue(navigateMock);
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    afterAll(() => {
        jest.useRealTimers();
    });

    test('uses the default timeout of 60000 and calls logout + navigate', () => {
        render(<SessionTimeout logout={logoutMock} />);
        // Initially, neither logout nor navigate should be called
        expect(logoutMock).not.toHaveBeenCalled();
        expect(navigateMock).not.toHaveBeenCalled();

        act(() => {
            jest.advanceTimersByTime(60000);
        });

        expect(logoutMock).toHaveBeenCalledTimes(1);
        expect(navigateMock).toHaveBeenCalledWith('/login');
    });

    test('uses a custom timeout and calls logout + navigate after that time', () => {
        render(<SessionTimeout logout={logoutMock} timeout={2000} />);
        expect(logoutMock).not.toHaveBeenCalled();
        expect(navigateMock).not.toHaveBeenCalled();

        act(() => {
            jest.advanceTimersByTime(2000);
        });

        expect(logoutMock).toHaveBeenCalledTimes(1);
        expect(navigateMock).toHaveBeenCalledWith('/login');
    });

    test('resets the timer on user events (e.g., mousemove)', () => {
        render(<SessionTimeout logout={logoutMock} timeout={3000} />);

        act(() => {
            jest.advanceTimersByTime(2000);
        });
        expect(logoutMock).not.toHaveBeenCalled();

        act(() => {
            const mouseMoveEvent = new Event('mousemove');
            window.dispatchEvent(mouseMoveEvent);
            // Advance 3000ms again after reset
            jest.advanceTimersByTime(3000);
        });

        expect(logoutMock).toHaveBeenCalledTimes(1);
        expect(navigateMock).toHaveBeenCalledWith('/login');
    });

    test('cleans up on unmount (removes timers and event listeners)', () => {
        const { unmount } = render(
            <SessionTimeout logout={logoutMock} timeout={1000} />
        );

        unmount();

        act(() => {
            jest.advanceTimersByTime(1000);
        });

        expect(logoutMock).not.toHaveBeenCalled();
        expect(navigateMock).not.toHaveBeenCalled();
    });

    test('cleanup handles null timerId.current (if setTimeout returns null)', () => {
        jest.spyOn(global, 'setTimeout').mockImplementation(() => null);
        const clearTimeoutSpy = jest.spyOn(global, 'clearTimeout');

        const { unmount } = render(
            <SessionTimeout logout={logoutMock} timeout={1000} />
        );

        unmount();

        expect(clearTimeoutSpy).not.toHaveBeenCalled();

        global.setTimeout.mockRestore();
        global.clearTimeout.mockRestore();
    });
});

