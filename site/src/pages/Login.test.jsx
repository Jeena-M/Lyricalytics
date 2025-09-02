import React from "react";
import {
    render,
    fireEvent,
    waitFor,
    screen,
    act
} from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import "./TestMocks";
import Login from "./Login";

jest.mock("../components/LoginButton", () => (props) => (
    <button data-testid="login-button" type="submit" onClick={props.onClick}>
        Login
    </button>
));

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
}));

describe("Login Component", () => {
    let mockLogin;

    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = jest.fn();
        mockLogin = jest.fn(); // the login prop
        jest.spyOn(console, "error").mockImplementation(() => {});
    });

    afterEach(() => {
        jest.resetAllMocks();
    });

    function setup() {
        return render(
            <MemoryRouter>
                <Login login={mockLogin} />
            </MemoryRouter>
        );
    }

    it("calls login prop and successfully logs in (data.isLoggedIn = true), then navigates after 1s", async () => {
        jest.useFakeTimers();

        global.fetch.mockResolvedValue({
            ok: true,
            json: async () => ({ isLoggedIn: true, username: "TestUser" }),
        });

        setup();

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "user" },
        });
        fireEvent.change(screen.getByTestId("password"), {
            target: { value: "pass" },
        });

        fireEvent.click(screen.getByTestId("login-button"));

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalledWith(
                "/login",
                expect.objectContaining({
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username: "user", password: "pass" }),
                })
            );
        });

        await waitFor(() => expect(mockLogin).toHaveBeenCalled());

        expect(await screen.findByTestId("toast")).toHaveTextContent(
            "Welcome back, TestUser!"
        );

        act(() => {
            jest.runAllTimers();
        });

        expect(mockNavigate).toHaveBeenCalledWith("/search");
    });

    it("does not call login prop when login fails (data.isLoggedIn = false)", async () => {
        global.fetch.mockResolvedValue({
            ok: true,
            json: async () => ({ isLoggedIn: false }),
        });

        setup();

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "user" },
        });
        fireEvent.change(screen.getByTestId("password"), {
            target: { value: "wrong" },
        });

        fireEvent.click(screen.getByTestId("login-button"));

        await waitFor(() => {
            expect(global.fetch).toHaveBeenCalled();
        });

        // Since login is only called on success, it should not be called here.
        expect(mockLogin).not.toHaveBeenCalled();

        expect(await screen.findByTestId("toast")).toHaveTextContent(
            "Login failed. Please check your credentials."
        );

        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("handles non-ok response with fallback status message", async () => {
        global.fetch.mockResolvedValue({
            ok: false,
            status: 403,
            headers: { get: () => null },
            json: async () => ({}),
        });

        setup();

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "forbiddenUser" },
        });
        fireEvent.change(screen.getByTestId("password"), {
            target: { value: "forbiddenPass" },
        });

        fireEvent.click(screen.getByTestId("login-button"));

        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent(
                "An error occurred during login. HTTP error: 403"
            );
        });

        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("closes the toast when close button is clicked", async () => {
        global.fetch.mockResolvedValue({
            ok: true,
            json: async () => ({ isLoggedIn: false }),
        });

        setup();

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "user" },
        });
        fireEvent.change(screen.getByTestId("password"), {
            target: { value: "wrong" },
        });
        fireEvent.click(screen.getByTestId("login-button"));

        await screen.findByTestId("toast");

        fireEvent.click(screen.getByTestId("close-toast"));
        await waitFor(() => {
            expect(screen.queryByTestId("toast")).toBeNull();
        });
    });

    it("calls the setConfirmPassword inline function when confirm-password input is changed", () => {
        setup();
        const confirmInput = screen.getByTestId("confirm-password");

        // Fire a change event; even though the inline function is a no-op,
        // calling it will cover that lambda.
        fireEvent.change(confirmInput, { target: { value: "newpass" } });

        // Since Login doesn't update confirmPassword, the input value remains unchanged.
        expect(confirmInput.value).toBe("");
    });

    it("handles account locked error and updates toast after 30 seconds", async () => {
        jest.useFakeTimers();

        // Simulate a fetch rejection with an error message that includes "Account locked"
        global.fetch.mockRejectedValue(new Error("Account locked: too many attempts"));

        setup();

        // Fill in the username and password fields
        fireEvent.change(screen.getByTestId("username"), { target: { value: "lockedUser" } });
        fireEvent.change(screen.getByTestId("password"), { target: { value: "wrongpass" } });

        // Click the login button to trigger the fetch and error catch branch
        fireEvent.click(screen.getByTestId("login-button"));

        // Wait for the locked account toast to appear
        const lockedToast = await screen.findByTestId("toast");
        expect(lockedToast).toHaveTextContent(
            "Account locked due to multiple failed login attempts. Please try again in 30 seconds."
        );

        // Fast-forward time by 30 seconds
        act(() => {
            jest.advanceTimersByTime(30 * 1000);
        });

        // Wait for the toast to update to the unlocked message
        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent("Account unlocked. Try again.");
        });
    });
});
