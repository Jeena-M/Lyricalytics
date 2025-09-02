import React from 'react';
import {
    render,
    screen,
    waitFor,
    act,
    fireEvent,
    within,
} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Favorites, { handleMoveSongHelper } from './Favorites';

jest.mock('../components/Team', () => () => <div data-testid="team" />);
jest.mock('../components/Toast', () => ({ message, onClose }) => (
    <div data-testid="toast">
        {message}
        <button onClick={onClose}>Close Toast</button>
    </div>
));
jest.mock(
    '../components/Modal',
    () => ({ isOpen, onClose, onSubmit, title, confirmText, cancelText, children }) => (
        <div data-testid={`modal-${title}`}>
            {children}
            {confirmText && <button onClick={onSubmit}>{confirmText}</button>}
            {cancelText && <button onClick={onClose}>{cancelText}</button>}
        </div>
    )
);

beforeEach(() => {
    jest.spyOn(Storage.prototype, 'getItem').mockImplementation((key) => {
        if (key === 'username') return 'testuser';
        return null;
    });
});

afterEach(() => {
    jest.restoreAllMocks();
    jest.clearAllMocks();
});

test('handles no username case', async () => {
    jest.spyOn(Storage.prototype, 'getItem').mockReturnValueOnce('');
    // only privacy fetch will be called
    global.fetch = jest.fn().mockResolvedValueOnce({
        ok: true,
        json: async () => ({ isPrivate: false }),
    });
    render(<Favorites />);
    // should show username-required toast
    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Username is required to fetch favorites.');
    // should show no-favorites message
    expect(screen.getByText('No favorite songs found.')).toBeInTheDocument();
});

test('loads favorites and privacy toggle on mount', async () => {
    global.fetch = jest.fn()
        // getFavoritesSongs
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Song1', artist: 'Artist1', year: 2020 }],
        })
        // fetchPrivacy
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ isPrivate: true }),
        });

    render(<Favorites />);

    // favorites table should render
    expect(await screen.findByText('Song1')).toBeInTheDocument();
    // since isPrivate true => publicAcc false => "Account Private" active
    const privateBtn = screen.getByRole('button', { name: 'Account Private' });
    expect(privateBtn).toHaveClass('bg-purple-700');
});

test('handles getFavorites error and privacy error', async () => {
    // getFavoritesSongs fails
    global.fetch = jest.fn()
        .mockResolvedValueOnce({
            ok: false,
            headers: { get: () => 'Fetch Error' },
        })
        // privacy fails
        .mockResolvedValueOnce({ ok: false });

    jest.spyOn(console, 'error').mockImplementation(() => {});

    render(<Favorites />);

    // toast from fetchFavoriteSongs
    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Fetch Error');
    // privacy failure should log error
    await waitFor(() => expect(console.error).toHaveBeenCalled());
});

test('toggle privacy success', async () => {
    global.fetch = jest.fn()
        // getFavoritesSongs
        .mockResolvedValueOnce({ ok: true, json: async () => [] })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        // togglePrivacyMode
        .mockResolvedValueOnce({ ok: true, text: async () => 'Toggled' });

    render(<Favorites />);

    // wait for initial no-favorites state
    await screen.findByText('No favorite songs found.');

    userEvent.click(screen.getByRole('button', { name: 'Account Public' }));
    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Toggled');
});

test('toggle privacy error', async () => {
    global.fetch = jest.fn()
        .mockResolvedValueOnce({ ok: true, json: async () => [] })
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        .mockResolvedValueOnce({ ok: false, text: async () => 'Toggle Error' });

    render(<Favorites />);
    await screen.findByText('No favorite songs found.');

    userEvent.click(screen.getByRole('button', { name: 'Account Private' }));
    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Toggle Error');
});

test('song detail success and shows year', async () => {
    global.fetch = jest.fn()
        // getFavoritesSongs
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Song1', artist: 'Artist1', year: 2020 }],
        })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        // song details
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ title: 'Song1', artist: 'Artist1', year: 2020 }),
        });

    render(<Favorites />);
    const cell = await screen.findByText('Song1');
    userEvent.click(cell);

    expect(await screen.findByText('Artist:')).toBeInTheDocument();
    expect(screen.getByText('Artist1')).toBeInTheDocument();
    expect(screen.getByText('Year:')).toBeInTheDocument();
    expect(screen.getByText('2020')).toBeInTheDocument();
});

test('song detail no year branch', async () => {
    global.fetch = jest.fn()
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Song1', artist: 'Artist1' }],
        })
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ title: 'Song1', artist: 'Artist1' }),
        });

    render(<Favorites />);
    const cell = await screen.findByText('Song1');
    userEvent.click(cell);

    expect(await screen.findByText('Artist:')).toBeInTheDocument();
    expect(screen.getByText('Artist1')).toBeInTheDocument();
    expect(screen.queryByText(/Year:/)).toBeNull();
});

test('hover events else branch and mouse leave', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Song1', artist: 'Artist1' }],
        })
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    const cell = await screen.findByText('Song1');

    // override to make distance >= 40
    Object.defineProperty(cell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 0, height: 0 }),
    });

    fireEvent.mouseMove(cell, { clientX: 50, clientY: 0 }); // else branch
    fireEvent.mouseLeave(cell); // clearTimeout

    jest.useRealTimers();
});

test('delete all favorites success', async () => {
    global.fetch = jest.fn()
        // getFavoritesSongs
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Song1', artist: 'Artist1' }],
        })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        // deleteAllSongs
        .mockResolvedValueOnce({ ok: true, text: async () => '' });

    render(<Favorites />);
    await screen.findByText('Song1');

    userEvent.click(screen.getByRole('button', { name: 'Delete All Favorites' }));
    const modal = screen.getByTestId('modal-Delete All Favorites?');
    const confirmBtn = within(modal).getByRole('button', { name: 'Delete' });
    userEvent.click(confirmBtn);

    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('All favorites deleted.');
});

test('delete all favorites error', async () => {
    global.fetch = jest.fn()
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Song1', artist: 'Artist1' }],
        })
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        .mockResolvedValueOnce({ ok: false, text: async () => 'Delete Error' });

    render(<Favorites />);
    await screen.findByText('Song1');

    userEvent.click(screen.getByRole('button', { name: 'Delete All Favorites' }));
    const modal = screen.getByTestId('modal-Delete All Favorites?');
    const confirmBtn = within(modal).getByRole('button', { name: 'Delete' });
    userEvent.click(confirmBtn);

    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Delete Error');
});

test('delete single song success', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        // getFavoritesSongs
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Song1', artist: 'Artist1' }],
        })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        // deleteOneSong
        .mockResolvedValueOnce({ ok: true, text: async () => '' });

    render(<Favorites />);
    const cell = await screen.findByText('Song1');

    // trigger hover to open action modal
    Object.defineProperty(cell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 100, height: 100 }),
    });
    fireEvent.mouseMove(cell, { clientX: 50, clientY: 50 });
    act(() => jest.advanceTimersByTime(400));

    // click Delete in action modal
    const actionModal = screen.getByTestId('modal-Actions for "Song1"');
    const deleteBtn = within(actionModal).getByRole('button', { name: 'Delete' });
    userEvent.click(deleteBtn);

    // confirm delete in confirm modal
    const confirmModal = screen.getByTestId('modal-Confirm Delete');
    const confirmBtn = within(confirmModal).getByRole('button', { name: 'Delete' });
    userEvent.click(confirmBtn);

    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Deleted "Song1" by Artist1');

    jest.useRealTimers();
});

test('delete single song error', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Song1', artist: 'Artist1' }],
        })
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        .mockResolvedValueOnce({ ok: false, text: async () => 'Delete Song Error' });

    render(<Favorites />);
    const cell = await screen.findByText('Song1');

    Object.defineProperty(cell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 100, height: 100 }),
    });
    fireEvent.mouseMove(cell, { clientX: 50, clientY: 50 });
    act(() => jest.advanceTimersByTime(400));

    const actionModal = screen.getByTestId('modal-Actions for "Song1"');
    const deleteBtn = within(actionModal).getByRole('button', { name: 'Delete' });
    userEvent.click(deleteBtn);

    const confirmModal = screen.getByTestId('modal-Confirm Delete');
    const confirmBtn = within(confirmModal).getByRole('button', { name: 'Delete' });
    userEvent.click(confirmBtn);

    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Delete Song Error');

    jest.useRealTimers();
});

test('handleMoveSongHelper does nothing if no pending', async () => {
    const setToast = jest.fn();
    const fetchFavorites = jest.fn();
    const setDeleteModalOpen = jest.fn();
    await handleMoveSongHelper({
        pendingDeleteSong: null,
        username: 'user',
        direction: 'up',
        fetchFn: jest.fn(),
        setToast,
        fetchFavorites,
        setDeleteModalOpen,
    });
    expect(setToast).not.toHaveBeenCalled();
    expect(fetchFavorites).not.toHaveBeenCalled();
    expect(setDeleteModalOpen).not.toHaveBeenCalled();
});

test('handleMoveSongHelper success', async () => {
    const pendingDeleteSong = { title: 'Song1', artist: 'Artist1' };
    const setToast = jest.fn();
    const fetchFavorites = jest.fn();
    const setDeleteModalOpen = jest.fn();
    const fetchFn = jest.fn().mockResolvedValueOnce({ ok: true });

    await handleMoveSongHelper({
        pendingDeleteSong,
        username: 'user',
        direction: 'down',
        fetchFn,
        setToast,
        fetchFavorites,
        setDeleteModalOpen,
    });

    expect(fetchFn).toHaveBeenCalledWith(
        '/api/favorites/moveSongDown',
        expect.objectContaining({
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: 'user',
                songName: 'Song1',
                artistName: 'Artist1',
            }),
        })
    );
    expect(setToast).toHaveBeenCalledWith({
        message: 'Moved "Song1" down',
        type: 'success',
    });
    expect(fetchFavorites).toHaveBeenCalled();
    expect(setDeleteModalOpen).toHaveBeenCalledWith(false);
});

test('handleMoveSongHelper error', async () => {
    const pendingDeleteSong = { title: 'Song1', artist: 'Artist1' };
    const setToast = jest.fn();
    const fetchFavorites = jest.fn();
    const setDeleteModalOpen = jest.fn();
    const fetchFn = jest.fn().mockResolvedValueOnce({
        ok: false,
        text: async () => 'Move Error',
    });

    await handleMoveSongHelper({
        pendingDeleteSong,
        username: 'user',
        direction: 'up',
        fetchFn,
        setToast,
        fetchFavorites,
        setDeleteModalOpen,
    });

    expect(setToast).toHaveBeenCalledWith({
        message: 'Move Error',
        type: 'error',
    });
    expect(fetchFavorites).not.toHaveBeenCalled();
    expect(setDeleteModalOpen).toHaveBeenCalledWith(false);
});

import * as Fav from './Favorites'; // for spying on handleMoveSongHelper

// 1) fetchFavoriteSongs: header.get() returns null → default "Failed to load favorite songs."
test('fetchFavoriteSongs header null uses default error message', async () => {
    global.fetch = jest.fn()
        // getFavoritesSongs: ok: false, headers.get → null
        .mockResolvedValueOnce({ ok: false, headers: { get: () => null } })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Failed to load favorite songs.');
});

// 2) fetchFavoriteSongs throws → fallback "Error fetching favorite songs."
test('fetchFavoriteSongs network error falls back to generic message', async () => {
    global.fetch = jest.fn()
        // getFavoritesSongs rejects
        .mockRejectedValueOnce({})
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Error fetching favorite songs.');
});

// 3) deleteAllFavorites rejects → fallback "Failed to delete favorites."
test('handleDeleteAllFavorites network error falls back to generic message', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        // getFavoritesSongs success
        .mockResolvedValueOnce({ ok: true, json: async () => [{ title: 'A', artist: 'B' }] })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        // deleteAllSongs rejects
        .mockRejectedValueOnce({});

    render(<Favorites />);
    await screen.findByText('A');

    userEvent.click(screen.getByRole('button', { name: 'Delete All Favorites' }));
    const modal = screen.getByTestId('modal-Delete All Favorites?');
    const btn = within(modal).getByRole('button', { name: 'Delete' });
    userEvent.click(btn);

    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Failed to delete favorites.');

    jest.useRealTimers();
});

// 4) deleteOneSong rejects → fallback "Failed to delete song."
test('handleDeleteSong network error falls back to generic message', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        // getFavoritesSongs
        .mockResolvedValueOnce({ ok: true, json: async () => [{ title: 'X', artist: 'Y' }] })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        // deleteOneSong rejects
        .mockRejectedValueOnce({});

    render(<Favorites />);
    const cell = await screen.findByText('X');
    // open action modal
    Object.defineProperty(cell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 100, height: 100 }),
    });
    fireEvent.mouseMove(cell, { clientX: 50, clientY: 50 });
    act(() => jest.advanceTimersByTime(400));

    // click Delete
    const actionModal = screen.getByTestId(`modal-Actions for "X"`);
    userEvent.click(within(actionModal).getByRole('button', { name: 'Delete' }));
    // confirm
    const confirmModal = screen.getByTestId('modal-Confirm Delete');
    userEvent.click(within(confirmModal).getByRole('button', { name: 'Delete' }));

    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Failed to delete song.');

    jest.useRealTimers();
});

// 5) togglePrivacy rejects → fallback "Failed to toggle privacy."
test('handleTogglePrivacy network error falls back to generic message', async () => {
    global.fetch = jest.fn()
        // getFavoritesSongs
        .mockResolvedValueOnce({ ok: true, json: async () => [] })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        // togglePrivacyMode rejects
        .mockRejectedValueOnce({});

    render(<Favorites />);
    await screen.findByText('No favorite songs found.');

    userEvent.click(screen.getByRole('button', { name: 'Account Private' }));
    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Failed to toggle privacy.');
});


// 2) handleSongTitleClick: server-error branch
test('song-details click shows server-error message when res.ok is false', async () => {
    jest.spyOn(console, 'error').mockImplementation(() => {});
    global.fetch = jest.fn()
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'X', artist: 'Y' }],
        })
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ isPrivate: false }),
        })
        .mockResolvedValueOnce({
            ok: false,
            text: async () => 'Detail Error',
        });

    render(<Favorites />);
    const cell = await screen.findByText('X');
    userEvent.click(cell);

    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Detail Error');
});

// 3) handleSongTitleClick: network-error branch
test('song-details click shows fallback message on network failure', async () => {
    jest.spyOn(console, 'error').mockImplementation(() => {});
    global.fetch = jest.fn()
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Z', artist: 'W' }],
        })
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ isPrivate: false }),
        })
        .mockRejectedValueOnce(new Error('boom'));

    render(<Favorites />);
    const cell = await screen.findByText('Z');
    userEvent.click(cell);

    const toast = await screen.findByTestId('toast');
    // err.message == 'boom' is shown
    expect(toast).toHaveTextContent('boom');
});

test('closing song-details modal removes its contents', async () => {
    jest.spyOn(console, 'error').mockImplementation(() => {});
    global.fetch = jest.fn()
        // favorites
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [{ title: 'Song3', artist: 'Artist3', year: 2021 }],
        })
        // privacy
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ isPrivate: false }),
        })
        // first details fetch
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ title: 'Song3', artist: 'Artist3', year: 2021 }),
        })
        // second details fetch for reopen
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ title: 'Song3', artist: 'Artist3', year: 2021 }),
        });

    render(<Favorites />);
    const cell = await screen.findByText('Song3');
    userEvent.click(cell);

    const detailModal = await screen.findByTestId('modal-Details for "Song3"');
    expect(within(detailModal).getByText('Artist:')).toBeInTheDocument();

    // close via "Close"
    userEvent.click(within(detailModal).getByRole('button', { name: 'Close' }));
    await waitFor(() =>
        expect(screen.queryByText('Artist:')).toBeNull()
    );

    // reopen
    userEvent.click(cell);
    const detailModal2 = await screen.findByTestId('modal-Details for "Song3"');
    // close via "Back"
    userEvent.click(within(detailModal2).getByRole('button', { name: 'Back' }));
    await waitFor(() =>
        expect(screen.queryByText('Artist:')).toBeNull()
    );
});

test('clicking Move Up calls moveSongUp endpoint and shows success', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        // 1st call: fetchFavoriteSongs
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [
                { title: 'First',  artist: 'A' },
                { title: 'Second', artist: 'B' },
            ],
        })
        // 2nd call: fetchPrivacy
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ isPrivate: false }),
        })
        // 3rd call: moveSongUp
        .mockResolvedValueOnce({
            ok: true,
            text: async () => '',
        })
        // 4th call: fetchFavoriteSongs again inside handleMoveSongHelper
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [
                { title: 'First',  artist: 'A' },
                { title: 'Second', artist: 'B' },
            ],
        });

    render(<Favorites />);
    const secondCell = await screen.findByText('Second');

    Object.defineProperty(secondCell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 100, height: 100 }),
    });
    fireEvent.mouseMove(secondCell, { clientX: 50, clientY: 50 });
    act(() => jest.advanceTimersByTime(400));

    const upBtn = screen.getByRole('button', { name: 'Move Up' });
    userEvent.click(upBtn);

    await waitFor(() => {
        expect(global.fetch).toHaveBeenCalledWith(
            '/api/favorites/moveSongUp',
            expect.objectContaining({ method: 'POST' })
        );
    });

    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Moved "Second" up');

    jest.useRealTimers();
});

// 1) handleMoveSongHelper: fallback error message when err.message is undefined
test('handleMoveSongHelper uses default error message if err.message is falsy', async () => {
    const setToast = jest.fn();
    const fetchFavorites = jest.fn();
    const setDeleteModalOpen = jest.fn();
    await handleMoveSongHelper({
        pendingDeleteSong: { title: 'S', artist: 'A' },
        username: 'u',
        direction: 'down',
        fetchFn: jest.fn().mockRejectedValue({}),
        setToast,
        fetchFavorites,
        setDeleteModalOpen,
    });
    expect(setToast).toHaveBeenCalledWith({
        message: 'Failed to move song down',
        type: 'error',
    });
    expect(setDeleteModalOpen).toHaveBeenCalledWith(false);
});

// 2) handleSongTitleClick: fallback “Failed to load song details.” when err.message is undefined
test('song-details click shows default error when thrown error has no message', async () => {
    global.fetch = jest.fn()
        .mockResolvedValueOnce({ ok: true, json: async () => [{ title: 'T', artist: 'U' }] })
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) })
        .mockRejectedValueOnce({}); // err.message undefined

    render(<Favorites />);
    const cell = await screen.findByText('T');
    userEvent.click(cell);
    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Failed to load song details.');
});

// 3) Toast onClose clears the toast
test('clicking the Toast close button removes the toast', async () => {
    // force a toast via fetchFavoriteSongs error
    global.fetch = jest.fn()
        .mockResolvedValueOnce({ ok: false, headers: { get: () => null } })
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    const toast = await screen.findByTestId('toast');
    userEvent.click(within(toast).getByRole('button', { name: 'Close Toast' }));
    await waitFor(() => expect(screen.queryByTestId('toast')).toBeNull());
});

// 4) Cancel on Delete All should NOT call deleteAllSongs and should NOT show a toast
test('Cancel on Delete All does not delete anything or show toast', async () => {
    global.fetch = jest.fn()
        // 1) fetchFavoriteSongs
        .mockResolvedValueOnce({ ok: true, json: async () => [] })
        // 2) fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    await screen.findByText('No favorite songs found.');

    // open and immediately cancel
    userEvent.click(screen.getByRole('button', { name: 'Delete All Favorites' }));
    const deleteAllModal = screen.getByTestId('modal-Delete All Favorites?');
    userEvent.click(within(deleteAllModal).getByRole('button', { name: 'Cancel' }));

    // still only the two initial fetches, no delete call
    expect(global.fetch).toHaveBeenCalledTimes(2);
    // and no toast rendered
    expect(screen.queryByTestId('toast')).toBeNull();
});

// 5) Cancel on Confirm Delete should NOT call deleteOneSong and should NOT show a toast
test('Cancel on Confirm Delete does not delete single song or show toast', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        // 1) fetchFavoriteSongs
        .mockResolvedValueOnce({ ok: true, json: async () => [{ title: 'A', artist: 'B' }] })
        // 2) fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    const cell = await screen.findByText('A');

    // open actions
    Object.defineProperty(cell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 100, height: 100 }),
    });
    fireEvent.mouseMove(cell, { clientX: 50, clientY: 50 });
    act(() => jest.advanceTimersByTime(400));

    // enter confirm-delete flow, then cancel
    const actionModal = screen.getByTestId('modal-Actions for "A"');
    userEvent.click(within(actionModal).getByRole('button', { name: 'Delete' }));
    const confirmModal = await screen.findByTestId('modal-Confirm Delete');
    userEvent.click(within(confirmModal).getByRole('button', { name: 'Cancel' }));

    // still only the two initial fetches
    expect(global.fetch).toHaveBeenCalledTimes(2);
    expect(screen.queryByTestId('toast')).toBeNull();

    jest.useRealTimers();
});

// 1) Delete All “Cancel” should not fire the delete API or show a toast
test('clicking Cancel on Delete All modal does not call deleteAllSongs or show a toast', async () => {
    global.fetch = jest.fn()
        // fetchFavoriteSongs
        .mockResolvedValueOnce({ ok: true, json: async () => [] })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    await screen.findByText('No favorite songs found.');

    // open the Delete All modal
    userEvent.click(screen.getByRole('button', { name: 'Delete All Favorites' }));
    const deleteAllModal = screen.getByTestId('modal-Delete All Favorites?');

    // click Cancel
    await userEvent.click(within(deleteAllModal).getByRole('button', { name: 'Cancel' }));

    // should still have only the two initial fetch calls (no deleteAllSongs)
    expect(global.fetch).toHaveBeenCalledTimes(2);

    // and no toast should appear
    expect(screen.queryByTestId('toast')).toBeNull();
});

// 2) Cancel on Confirm-Delete resets pendingDeleteSong (goes back to undefined)
test('clicking Cancel on Confirm Delete resets pendingDeleteSong', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        // getFavoritesSongs
        .mockResolvedValueOnce({ ok: true, json: async () => [{ title: 'A', artist: 'B' }] })
        // fetchPrivacy
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    const cell = await screen.findByText('A');

    // hover to show Actions modal
    Object.defineProperty(cell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 100, height: 100 }),
    });
    fireEvent.mouseMove(cell, { clientX: 50, clientY: 50 });
    act(() => jest.advanceTimersByTime(400));

    // in Actions modal for "A", click the child-Delete button
    const actionsModalA = screen.getByTestId('modal-Actions for "A"');
    userEvent.click(within(actionsModalA).getByRole('button', { name: 'Delete' }));

    // Confirm Delete modal appears
    const confirmModal = await screen.findByTestId('modal-Confirm Delete');
    // click its Cancel
    userEvent.click(within(confirmModal).getByRole('button', { name: 'Cancel' }));

    // after closing, the Actions modal title should revert back to undefined
    await waitFor(() =>
        expect(screen.getByTestId('modal-Actions for "undefined"')).toBeInTheDocument()
    );
    jest.useRealTimers();
});

// 3) Back in Actions modal clears pendingDeleteSong
test('clicking Back on Actions modal resets pendingDeleteSong', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        .mockResolvedValueOnce({ ok: true, json: async () => [{ title: 'X', artist: 'Y' }] })
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    const cell = await screen.findByText('X');

    // hover to show Actions modal
    Object.defineProperty(cell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 100, height: 100 }),
    });
    fireEvent.mouseMove(cell, { clientX: 50, clientY: 50 });
    act(() => jest.advanceTimersByTime(400));

    // click Back (cancelText) in the Actions modal for "X"
    const actionsModalX = screen.getByTestId('modal-Actions for "X"');
    userEvent.click(within(actionsModalX).getByRole('button', { name: 'Back' }));

    // title resets to undefined
    await waitFor(() =>
        expect(screen.getByTestId('modal-Actions for "undefined"')).toBeInTheDocument()
    );
    jest.useRealTimers();
});

// 4) Close in Actions modal also clears pendingDeleteSong
test('clicking Close on Actions modal resets pendingDeleteSong', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        .mockResolvedValueOnce({ ok: true, json: async () => [{ title: 'Z', artist: 'W' }] })
        .mockResolvedValueOnce({ ok: true, json: async () => ({ isPrivate: false }) });

    render(<Favorites />);
    const cell = await screen.findByText('Z');

    // hover to show Actions modal
    Object.defineProperty(cell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 100, height: 100 }),
    });
    fireEvent.mouseMove(cell, { clientX: 50, clientY: 50 });
    act(() => jest.advanceTimersByTime(400));

    // click Close (confirmText) in the Actions modal for "Z"
    const actionsModalZ = screen.getByTestId('modal-Actions for "Z"');
    userEvent.click(within(actionsModalZ).getByRole('button', { name: 'Close' }));

    // title resets to undefined
    await waitFor(() =>
        expect(screen.getByTestId('modal-Actions for "undefined"')).toBeInTheDocument()
    );
    jest.useRealTimers();
});

// clicking Move Down on a non-last item calls moveSongDown and shows success
test('clicking Move Down calls moveSongDown endpoint and shows success', async () => {
    jest.useFakeTimers();
    global.fetch = jest.fn()
        // 1) fetchFavoriteSongs with two items
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [
                { title: 'One', artist: 'A' },
                { title: 'Two', artist: 'B' },
            ],
        })
        // 2) fetchPrivacy
        .mockResolvedValueOnce({
            ok: true,
            json: async () => ({ isPrivate: false }),
        })
        // 3) moveSongDown
        .mockResolvedValueOnce({
            ok: true,
            text: async () => '',
        })
        // 4) fetchFavoriteSongs again inside helper
        .mockResolvedValueOnce({
            ok: true,
            json: async () => [
                { title: 'One', artist: 'A' },
                { title: 'Two', artist: 'B' },
            ],
        });

    render(<Favorites />);
    const firstCell = await screen.findByText('One');

    // hover to set pendingDeleteSong for index=0
    Object.defineProperty(firstCell, 'getBoundingClientRect', {
        value: () => ({ left: 0, top: 0, width: 100, height: 100 }),
    });
    fireEvent.mouseMove(firstCell, { clientX: 50, clientY: 50 });
    act(() => jest.advanceTimersByTime(400));

    // click Move Down
    const downBtn = screen.getByRole('button', { name: 'Move Down' });
    userEvent.click(downBtn);

    // verify the down endpoint was called
    await waitFor(() =>
        expect(global.fetch).toHaveBeenCalledWith(
            '/api/favorites/moveSongDown',
            expect.objectContaining({ method: 'POST' })
        )
    );

    // and the toast shows success
    const toast = await screen.findByTestId('toast');
    expect(toast).toHaveTextContent('Moved "One" down');

    jest.useRealTimers();
});

describe('Favorites onKeyDown handler', () => {
    beforeEach(() => {
        // enable fake timers to flush your 300ms timeout
        jest.useFakeTimers();

        // mock the three sequential fetch calls:
        global.fetch = jest
            .fn()
            // getFavoritesSongs
            .mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve([{title: 'TestSong', artist: 'TestArtist'}]),
            })
            // fetchPrivacy
            .mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve({isPrivate: false}),
            })
            // songDetailsFavorites
            .mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve({title: 'TestSong', artist: 'TestArtist', year: 2020}),
            });
    });

    afterEach(() => {
        // flush any pending timers
        act(() => jest.runOnlyPendingTimers());
        jest.useRealTimers();
    });

    it('opens the song-detail modal when Enter is pressed', async () => {
        render(<Favorites/>);

        // wait for the title-button to appear
        const btn = await screen.findByRole('button', {
            name: /View details for TestSong by TestArtist/i,
        });

        // simulate Enter keydown
        btn.focus();
        fireEvent.keyDown(btn, {key: 'Enter', code: 'Enter', charCode: 13});

        // flush your setTimeout
        act(() => jest.runAllTimers());

        // the Modal mock renders a <div data-testid={`modal-${title}`}>
        const modal = await screen.findByTestId('modal-Details for "TestSong"');
        expect(modal).toBeInTheDocument();

        // verify modal children rendered
        expect(within(modal).getByText(/Artist:/)).toBeInTheDocument();
        expect(within(modal).getByText(/TestArtist/)).toBeInTheDocument();

        // ensure the details-endpoint was called
        expect(global.fetch).toHaveBeenLastCalledWith(
            '/api/wordcloud/songDetailsFavorites',
            expect.any(Object)
        );
    });

    it('opens the song-detail modal when Space is pressed', async () => {
        render(<Favorites/>);

        const btn = await screen.findByRole('button', {
            name: /View details for TestSong by TestArtist/i,
        });

        btn.focus();
        fireEvent.keyDown(btn, {key: ' ', code: 'Space', charCode: 32});

        act(() => jest.runAllTimers());

        const modal = await screen.findByTestId('modal-Details for "TestSong"');
        expect(modal).toBeInTheDocument();

        expect(within(modal).getByText(/Year:/)).toBeInTheDocument();
        expect(within(modal).getByText(/2020/)).toBeInTheDocument();

        expect(global.fetch).toHaveBeenLastCalledWith(
            '/api/wordcloud/songDetailsFavorites',
            expect.any(Object)
        );
    });

    it('does not open the details modal when a non-Enter/Space key is pressed', async () => {
        // stub the two initial fetches: favorites list + privacy
        global.fetch = jest
            .fn()
            .mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve([{title: 'TestSong', artist: 'TestArtist'}]),
            })
            .mockResolvedValueOnce({
                ok: true,
                json: () => Promise.resolve({isPrivate: false}),
            });

        render(<Favorites/>);

        // wait for the song-title button to appear
        const btn = await screen.findByRole('button', {
            name: /View details for TestSong by TestArtist/i,
        });

        // press a different key
        btn.focus();
        fireEvent.keyDown(btn, {key: 'Escape', code: 'Escape', charCode: 27});

        // flush the 300ms click-reset timer
        act(() => jest.runAllTimers());

        // the details-modal should NOT be in the document
        expect(
            screen.queryByTestId('modal-Details for "TestSong"')
        ).toBeNull();
    });

    test('sr-only open-actions button click runs its onClick handler', async () => {
        global.fetch = jest.fn((url) => {
            if (url.includes('getFavoritesSongs')) {
                return Promise.resolve({
                    ok: true,
                    json: async () => [{ title: 'Shape of You', artist: 'Ed Sheeran' }],
                });
            }
            if (url.includes('privacy')) {
                return Promise.resolve({
                    ok: true,
                    json: async () => ({ isPrivate: false }),
                });
            }
            return Promise.resolve({ ok: true, json: async () => ({}) });
        });

        const { container } = render(<Favorites />);
        await screen.findByText('Shape of You');

        const hiddenBtn = container.querySelector(
            'button.sr-only[aria-label="Open actions for Shape of You"]'
        );
        expect(hiddenBtn).toBeInTheDocument();

        await act(async () => {
            fireEvent.click(hiddenBtn);
        });

        expect(
            screen.getByTestId(`modal-Actions for "Shape of You"`)
        ).toBeInTheDocument();
    });

})
