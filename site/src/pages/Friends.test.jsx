import React, {useState} from "react";
import {
    render,
    fireEvent,
    waitFor,
    screen,
    act
} from "@testing-library/react";
import { MemoryRouter, useLocation } from "react-router-dom";
import "./TestMocks";
import Friends from "./Friends";
import userEvent from "@testing-library/user-event";

jest.mock("../components/Team", () => () => <div data-testid="team-component">Team Component</div>);
jest.mock("../components/Modal", () => ({ isOpen, onClose, onSubmit, title, confirmText, cancelText, children }) => {
    if (!isOpen) return null;
    return (
        <div data-testid="modal">
            <h2>{title}</h2>
            <div>{children}</div>
            <button data-testid="modal-close" onClick={onClose}>
                {cancelText || "Close"}
            </button>
            <button data-testid="modal-submit" onClick={onSubmit}>
                {confirmText || "Submit"}
            </button>
        </div>
    );
});

jest.mock("../components/Toast", () => ({ message, type, onClose }) => (
    <div data-testid="toast" className={type}>
        {message}
        <button data-testid="close-toast" onClick={onClose}>
            Close
        </button>
    </div>
));

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useLocation: jest.fn(),
}));

describe("Friends Component", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = jest.fn();
        localStorage.clear();
        localStorage.setItem("username", "testUser");

        useLocation.mockReturnValue({ pathname: "/friends" });


        global.navigator.sendBeacon = jest.fn(() => true);
    });

    afterEach(() => {
        jest.resetAllMocks();
    });

    function setup() {
        return render(
            <MemoryRouter>
                <Friends />
            </MemoryRouter>
        );
    }

    it("renders the component correctly", () => {
        setup();
        expect(screen.getByText("Search Friends")).toBeInTheDocument();
        expect(screen.getByText("Submit")).toBeInTheDocument();
        expect(screen.getByText("Sort Ascending")).toBeInTheDocument();
        expect(screen.getByText("Sort Descending")).toBeInTheDocument();
    });



    it("fetches friend data successfully and displays results", async () => {

        const mockResponseData = {
            "Song 1": ["testUser", "friendUser"],
            "Song 2": ["friendUser"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();


        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friendUser" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song Title")).toBeInTheDocument();
            expect(screen.getByText("# of Lists Appeared In")).toBeInTheDocument();
            expect(screen.getByText("Song 1")).toBeInTheDocument();
            expect(screen.getByText("2")).toBeInTheDocument();
            expect(screen.getByText("Song 2")).toBeInTheDocument();
            expect(screen.getByText("1")).toBeInTheDocument();
        });
    });



    it("merges friend results from multiple searches", async () => {

        const firstResponse = {
            "Song 1": ["testUser", "friend1"],
            "Song 2": ["friend1"]
        };

        const secondResponse = {
            "Song 1": ["friend2"],
            "Song 3": ["testUser", "friend2"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => firstResponse
        }).mockResolvedValueOnce({
            ok: true,
            json: async () => secondResponse
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
            expect(screen.getByText("Song 2")).toBeInTheDocument();
        });

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend2" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
            expect(screen.getByText("Song 2")).toBeInTheDocument();
            expect(screen.getByText("Song 3")).toBeInTheDocument();
        });
    });

    it("sorts results in ascending order", async () => {
        const mockResponseData = {
            "Song A": ["testUser", "friend1", "friend2"],
            "Song B": ["testUser"],
            "Song C": ["testUser", "friend1"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song A")).toBeInTheDocument();
        });

        const rows = screen.getAllByRole('row');
        expect(rows[1].firstChild).toHaveTextContent("Song A");
        expect(rows[2].firstChild).toHaveTextContent("Song C");
        expect(rows[3].firstChild).toHaveTextContent("Song B");

        fireEvent.click(screen.getByText("Sort Ascending"));

        const rowsAfterSort = screen.getAllByRole('row');
        expect(rowsAfterSort[1].firstChild).toHaveTextContent("Song B");
        expect(rowsAfterSort[2].firstChild).toHaveTextContent("Song C");
        expect(rowsAfterSort[3].firstChild).toHaveTextContent("Song A");
    });

    it("sorts results in descending order", async () => {
        const mockResponseData = {
            "Song A": ["testUser", "friend1", "friend2"], // Count: 3
            "Song B": ["testUser"], // Count: 1
            "Song C": ["testUser", "friend1"] // Count: 2
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();


        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song A")).toBeInTheDocument();
        });


        fireEvent.click(screen.getByText("Sort Ascending"));


        fireEvent.click(screen.getByText("Sort Descending"));

        const rows = screen.getAllByRole('row');
        expect(rows[1].firstChild).toHaveTextContent("Song A");
        expect(rows[2].firstChild).toHaveTextContent("Song C");
        expect(rows[3].firstChild).toHaveTextContent("Song B");
    });

    it("shows friends modal when count cell is clicked", async () => {
        const mockResponseData = {
            "Song 1": ["testUser", "friend1", "friend2"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        const countCell = screen.getByText("3");
        fireEvent.click(countCell);

        await waitFor(() => {
            expect(screen.getByTestId("modal")).toBeInTheDocument();
            expect(screen.getByText('Friends who have "Song 1"')).toBeInTheDocument();
            expect(screen.getByText("testUser")).toBeInTheDocument();
            expect(screen.getByText("friend1")).toBeInTheDocument();
            expect(screen.getByText("friend2")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByTestId("modal-close"));

        await waitFor(() => {
            expect(screen.queryByTestId("modal")).not.toBeInTheDocument();
        });
    });

    it("shows song details modal when song title is clicked", async () => {
        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        const songDetailsResponse = {
            title: "Song 1",
            artist: "Test Artist",
            year: "2023"
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        }).mockResolvedValueOnce({
            ok: true,
            json: async () => songDetailsResponse
        });

        setup();


        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Song 1"));

        await waitFor(() => {
            expect(screen.getByTestId("modal")).toBeInTheDocument();
            expect(screen.getByText('Details for "Song 1"')).toBeInTheDocument();
            expect(screen.getByText("Artist:")).toBeInTheDocument();
            expect(screen.getByText("Test Artist", { exact: false })).toBeInTheDocument();
            expect(screen.getByText("Year:")).toBeInTheDocument();
            expect(screen.getByText("2023", { exact: false })).toBeInTheDocument();
        });
    });

    it("shows song details without year if year is not provided", async () => {
        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        const songDetailsResponse = {
            title: "Song 1",
            artist: "Test Artist"
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        }).mockResolvedValueOnce({
            ok: true,
            json: async () => songDetailsResponse
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Song 1"));

        await waitFor(() => {
            expect(screen.getByTestId("modal")).toBeInTheDocument();
            expect(screen.getByText("Artist:")).toBeInTheDocument();
            expect(screen.getByText("Test Artist", { exact: false })).toBeInTheDocument();
            expect(screen.queryByText("Year:")).not.toBeInTheDocument();
        });
    });

    it("handles error when fetching song details", async () => {
        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        }).mockResolvedValueOnce({
            ok: false,
            text: async () => "Failed to retrieve song details"
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Song 1"));

        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent("Failed to retrieve song details");
        });
    });

    it("closes song details modal on close button click", async () => {
        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        const songDetailsResponse = {
            title: "Song 1",
            artist: "Test Artist",
            year: "2023"
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        }).mockResolvedValueOnce({
            ok: true,
            json: async () => songDetailsResponse
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Song 1"));

        await waitFor(() => {
            expect(screen.getByTestId("modal")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByTestId("modal-submit"));

        await waitFor(() => {
            expect(screen.queryByTestId("modal")).not.toBeInTheDocument();
        });
    });

    it("cancels modal on back button click", async () => {
        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("2"));

        await waitFor(() => {
            expect(screen.getByTestId("modal")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByTestId("modal-close"));

        await waitFor(() => {
            expect(screen.queryByTestId("modal")).not.toBeInTheDocument();
        });
    });


    it("doesn't show hover modal when mouse moves away", async () => {
        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        const countCell = screen.getByText("2");

        const rect = { left: 100, top: 100, width: 50, height: 50 };
        jest.spyOn(countCell, 'getBoundingClientRect').mockImplementation(() => rect);

        fireEvent.mouseMove(countCell, {
            clientX: rect.left + rect.width/2,
            clientY: rect.top + rect.height/2
        });
        fireEvent.mouseLeave(countCell);

        jest.useFakeTimers();
        act(() => {
            jest.advanceTimersByTime(500);
        });

        expect(screen.queryByTestId("modal")).not.toBeInTheDocument();

        jest.useRealTimers();
    });



    it("handles beforeunload event", () => {
        setup();

        window.dispatchEvent(new Event('beforeunload'));

        expect(navigator.sendBeacon).toHaveBeenCalledWith(
            "/api/friends/clearComparisonMap",
            expect.any(Blob)
        );
    });

    it("doesn't break when no song results are available", async () => {
        // Mock an empty response
        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({})
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.queryByText("Song Title")).not.toBeInTheDocument();
            expect(screen.queryByText("# of Lists Appeared In")).not.toBeInTheDocument();
        });
    });

    it("handles mouse move events outside the hover distance", async () => {
        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        const countCell = screen.getByText("2");

        const rect = { left: 100, top: 100, width: 50, height: 50 };
        jest.spyOn(countCell, 'getBoundingClientRect').mockImplementation(() => rect);

        fireEvent.mouseMove(countCell, {
            clientX: rect.left + 100,
            clientY: rect.top + 100
        });

        jest.useFakeTimers();
        act(() => {
            jest.advanceTimersByTime(500);
        });

        expect(screen.queryByTestId("modal")).not.toBeInTheDocument();

        jest.useRealTimers();
    });

    it("prevents hover modal when recently clicked", async () => {
        jest.useFakeTimers();

        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        const countCell = screen.getByText("2");

        fireEvent.click(countCell);


        await waitFor(() => {
            expect(screen.getByTestId("modal")).toBeInTheDocument();
        });


        fireEvent.click(screen.getByTestId("modal-close"));

        await waitFor(() => {
            expect(screen.queryByTestId("modal")).not.toBeInTheDocument();
        });


        const rect = { left: 100, top: 100, width: 50, height: 50 };
        jest.spyOn(countCell, 'getBoundingClientRect').mockImplementation(() => rect);

        fireEvent.mouseMove(countCell, {
            clientX: rect.left + rect.width/2,
            clientY: rect.top + rect.height/2
        });


        act(() => {
            jest.advanceTimersByTime(200);
        });

        expect(screen.queryByTestId("modal")).not.toBeInTheDocument();

        jest.useRealTimers();
    });

    it("shows generic error toast when no _error message is returned", async () => {
        // Step 1: First render some results to ensure Toast component is mounted
        global.fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => ({
                    "Song 1": ["testUser", "friend1"]
                })
            })
            // Step 2: Then mock the failed fetch
            .mockResolvedValueOnce({
                ok: false,
                status: 500,
                json: async () => ({})
            });

        setup();

        // First successful fetch to populate song list (and make toast reachable)
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        // Now submit again with failing fetch
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "badFriend" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent("HTTP error: 500");
        });
    });


    it("shows fallback error message in catch block", async () => {
        // Step 1: First successful fetch to mount table & Toast
        global.fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => ({
                    "Song 1": ["testUser", "friend1"]
                })
            })
            // Step 2: Simulate fetch throwing error
            .mockImplementationOnce(() => {
                throw { toString: () => "some string", message: "" }; // fallback triggered
            });

        setup();

        // Trigger the first successful fetch
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        // Trigger the error-producing fetch
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "failFriend" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent("User not valid.");
        });
    });

    it("shows error toast when submitting an empty username", async () => {
        // First fetch: preload valid data to renderResultsTable and enable <Toast />
        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({
                "Song 1": ["testUser", "friend1"]
            })
        });

        setup();

        // Preload results
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        // Clear input to simulate empty submission
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent("Please enter a username.");
        });
    });

    it("shows error toast when user is not logged in", async () => {
        // Step 1: Preload valid data to trigger renderResultsTable and Toast component
        localStorage.setItem("username", "testUser"); // temporarily logged in
        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({
                "Song 1": ["testUser", "friend1"]
            })
        });

        setup();

        // Preload data
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        // Step 2: Clear username from localStorage to simulate logged out user
        localStorage.clear();

        // Step 3: Submit with any username
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "anyone" }
        });
        fireEvent.click(screen.getByText("Submit"));

        // Step 4: Toast should now be testable
        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent("You must be logged in.");
        });
    });

    it("shows modal after hovering near center with delay", async () => {
        jest.useFakeTimers();

        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        const countCell = screen.getByText("2");

        // Simulate hover near center
        const rect = { left: 100, top: 100, width: 50, height: 50 };
        jest.spyOn(countCell, 'getBoundingClientRect').mockImplementation(() => rect);

        fireEvent.mouseMove(countCell, {
            clientX: rect.left + rect.width / 2,
            clientY: rect.top + rect.height / 2
        });

        // Advance time to trigger hover delay
        act(() => {
            jest.advanceTimersByTime(400);
        });

        expect(screen.getByTestId("modal")).toBeInTheDocument();

        jest.useRealTimers();
    });

    it("resets clickedRecentlyRef after click delay", async () => {
        jest.useFakeTimers();

        const mockResponseData = {
            "Song 1": ["testUser", "friend1"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockResponseData
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        const countCell = screen.getByText("2");

        fireEvent.click(countCell);

        await waitFor(() => {
            expect(screen.getByTestId("modal")).toBeInTheDocument();
        });

        act(() => {
            jest.advanceTimersByTime(300);
        });

        jest.useRealTimers();
    });

    it("closes friends modal via submit (confirm) button", async () => {
        const mockData = {
            "Song X": ["testUser", "friendA"]
        };

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockData
        });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friendA" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song X")).toBeInTheDocument();
        });

        // Click on the count cell "2" (since "testUser" + "friendA" = 2)
        const countCell = screen.getByText("2");
        fireEvent.click(countCell);

        await waitFor(() => {
            expect(screen.getByTestId("modal")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByTestId("modal-submit"));

        await waitFor(() => {
            expect(screen.queryByTestId("modal")).not.toBeInTheDocument();
        });
    });


    it("closes song detail modal via close handler", async () => {
        const mockData = {
            "Song Y": ["testUser", "friendB"]
        };

        const songDetail = {
            title: "Song Y",
            artist: "Artist Y",
            year: "2022"
        };

        global.fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => mockData
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => songDetail
            });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friendB" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song Y")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Song Y"));

        await waitFor(() => {
            expect(screen.getByText("Details for \"Song Y\"")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByTestId("modal-close")); // triggers setSongDetailModalOpen(false) and setSongDetails(null)

        await waitFor(() => {
            expect(screen.queryByTestId("modal")).not.toBeInTheDocument();
        });
    });

    it("resets clickedRecentlyRef after song title click timeout", async () => {
        jest.useFakeTimers();

        const mockData = {
            "Song A": ["testUser", "friend1"]
        };

        const songDetails = {
            title: "Song A",
            artist: "Artist A",
            year: "2024"
        };

        global.fetch
            .mockResolvedValueOnce({ ok: true, json: async () => mockData })
            .mockResolvedValueOnce({ ok: true, json: async () => songDetails });

        setup();

        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song A")).toBeInTheDocument();
        });

        fireEvent.click(screen.getByText("Song A"));

        // Wait for the modal content to show up
        await waitFor(() => {
            expect(screen.getByText('Details for "Song A"')).toBeInTheDocument();
        });

        // Trigger the 300ms timeout to reset clickedRecentlyRef
        act(() => {
            jest.advanceTimersByTime(300);
        });

        // Confirm modal content is still present (we're not testing modal dismissal here)
        expect(screen.getByText('Details for "Song A"')).toBeInTheDocument();

        jest.useRealTimers();
    });

    it("clears toast on close button click", async () => {
        const mockResponse = {
            "Song 1": ["testUser", "friend1"]
        };

        // Step 1: First, preload song data so toast component is mounted
        global.fetch
            .mockResolvedValueOnce({ ok: true, json: async () => mockResponse })
            .mockResolvedValueOnce({ ok: false, status: 500, json: async () => ({}) });

        setup();

        // Step 2: Preload song results
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        // Step 3: Trigger failed fetch to show toast
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "badFriend" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByTestId("toast")).toBeInTheDocument();
        });

        // Step 4: Click the toast close button
        fireEvent.click(screen.getByTestId("close-toast"));

        // Step 5: Confirm it is removed
        await waitFor(() => {
            expect(screen.queryByTestId("toast")).not.toBeInTheDocument();
        });
    });

    it("shows fallback toast message when error has no message", async () => {
        // First call loads data normally so toast component mounts
        const mockResponse = {
            "Song 1": ["testUser", "friend1"]
        };

        global.fetch
            .mockResolvedValueOnce({ ok: true, json: async () => mockResponse })
            // Second call throws an error without a `message`
            .mockImplementationOnce(() => {
                throw { toString: () => "some string", message: "" };
            });

        setup();

        // Load song data first
        fireEvent.change(screen.getByPlaceholderText("type a username"), {
            target: { value: "friend1" }
        });
        fireEvent.click(screen.getByText("Submit"));

        await waitFor(() => {
            expect(screen.getByText("Song 1")).toBeInTheDocument();
        });

        // Click on song title to trigger error
        fireEvent.click(screen.getByText("Song 1"));

        // Toast should show fallback message
        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent("Failed to load song details.");
        });
    });

});