import React from "react";
import "./TestMocks"
import {
    render,
    fireEvent,
    waitFor,
    screen,
    act
} from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import "./TestMocks";
import Register from "./Register";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
}));

jest.mock("../components/CreateButton", () => (props) => (
    <button data-testid="create-button" type="submit" onClick={props.onClick}>
        Create
    </button>
));

jest.mock("../components/CancelButton", () => (props) => (
    <button data-testid="cancel-button" type="button" onClick={props.onClick}>
        Cancel
    </button>
));

jest.mock("../components/Modal", () => (props) => {
    if (!props.isOpen) return null;

    return (
        <div data-testid="modal">
            <h2>{props.title}</h2>
            {props.children}
            <button data-testid="modal-confirm" onClick={props.onSubmit}>
                {props.confirmText}
            </button>
            <button data-testid="modal-cancel" onClick={props.onClose}>
                {props.cancelText}
            </button>
        </div>
    );
});

describe("Register Component", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        global.fetch = jest.fn();
        jest.spyOn(console, "error").mockImplementation(() => {});
    });

    afterEach(() => {
        jest.resetAllMocks();
    });

    const fillAndSubmitForm = () => {
        render(
            <MemoryRouter>
                <Register />
            </MemoryRouter>
        );

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "user" },
        });
        fireEvent.change(screen.getByTestId("password"), {
            target: { value: "pass" },
        });
        fireEvent.change(screen.getByTestId("confirm-password"), {
            target: { value: "pass" },
        });

        fireEvent.click(screen.getByTestId("create-button"));
    };

    it("displays success toast on successful registration, clears inputs, and navigates after 1s", async () => {
        jest.useFakeTimers();

        const fakeResponse = { username: "testuser" };
        global.fetch.mockResolvedValue({
            ok: true,
            json: async () => fakeResponse,
        });

        render(
            <MemoryRouter>
                <Register />
            </MemoryRouter>
        );

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "testuser" },
        });
        fireEvent.change(screen.getByTestId("password"), {
            target: { value: "password123" },
        });
        fireEvent.change(screen.getByTestId("confirm-password"), {
            target: { value: "password123" },
        });

        fireEvent.click(screen.getByTestId("create-button"));

        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent(
                "Account created successfully!"
            );
        });

        expect(screen.getByTestId("username").value).toBe("");
        expect(screen.getByTestId("password").value).toBe("");
        expect(screen.getByTestId("confirm-password").value).toBe("");


        act(() => {
            jest.runAllTimers();
        });

        expect(mockNavigate).toHaveBeenCalledWith("/login");

        fireEvent.click(screen.getByTestId("close-toast"));
        await waitFor(() => {
            expect(screen.queryByTestId("toast")).toBeNull();
        });
    });

    it("displays error toast if password is present but confirmPassword is missing", async () => {
        global.fetch.mockResolvedValue({
            ok: true,
            json: async () => ({ username: "testuser" }),
        });

        render(
            <MemoryRouter>
                <Register />
            </MemoryRouter>
        );

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "testuser" },
        });
        fireEvent.change(screen.getByTestId("password"), {
            target: { value: "somePassword" },
        });
        fireEvent.change(screen.getByTestId("confirm-password"), {
            target: { value: "" },
        });

        fireEvent.click(screen.getByTestId("create-button"));

        expect(
            await screen.findByText("Please fill both password fields.")
        ).toBeInTheDocument();

        expect(global.fetch).not.toHaveBeenCalled();
    });

    it("displays error toast if password is missing but confirmPassword is present", async () => {
        global.fetch.mockResolvedValue({
            ok: true,
            json: async () => ({ username: "testuser" }),
        });

        render(
            <MemoryRouter>
                <Register />
            </MemoryRouter>
        );

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "testuser" },
        });
        fireEvent.change(screen.getByTestId("password"), {
            target: { value: "" },
        });
        fireEvent.change(screen.getByTestId("confirm-password"), {
            target: { value: "somePassword" },
        });

        fireEvent.click(screen.getByTestId("create-button"));

        expect(
            await screen.findByText("Please fill both password fields.")
        ).toBeInTheDocument();

        expect(global.fetch).not.toHaveBeenCalled();
    });

    it("displays error toast with custom header message when fetch returns non-ok response", async () => {
        global.fetch.mockResolvedValue({
            ok: false,
            status: 400,
            headers: { get: () => "Custom registration error" },
            json: async () => ({}),
        });

        fillAndSubmitForm();

        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent(
                "Registration failed. Custom registration error"
            );
        });
    });

    it("displays error toast with fallback message when header returns null", async () => {
        global.fetch.mockResolvedValue({
            ok: false,
            status: 400,
            headers: { get: () => null },
            json: async () => ({}),
        });

        fillAndSubmitForm();

        await waitFor(() => {
            expect(screen.getByTestId("toast")).toHaveTextContent(
                "Registration failed. HTTP error: 400"
            );
        });
    });

    it("opens the cancel modal when Cancel is clicked, closes it on 'No', and does NOT navigate", async () => {
        render(
            <MemoryRouter>
                <Register />
            </MemoryRouter>
        );

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "user" },
        });

        fireEvent.click(screen.getByTestId("cancel-button"));

        expect(screen.getByTestId("modal")).toBeInTheDocument();

        fireEvent.click(screen.getByTestId("modal-cancel"));

        await waitFor(() => {
            expect(screen.queryByTestId("modal")).toBeNull();
        });

        expect(screen.getByTestId("username").value).toBe("user");
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("clears inputs, shows cancellation toast, and navigates to /login after confirming cancellation", async () => {
        jest.useFakeTimers();
        render(
            <MemoryRouter>
                <Register />
            </MemoryRouter>
        );

        fireEvent.change(screen.getByTestId("username"), {
            target: { value: "user" },
        });
        fireEvent.change(screen.getByTestId("password"), {
            target: { value: "pass" },
        });
        fireEvent.change(screen.getByTestId("confirm-password"), {
            target: { value: "pass" },
        });

        fireEvent.click(screen.getByTestId("cancel-button"));
        expect(screen.getByTestId("modal")).toBeInTheDocument();

        fireEvent.click(screen.getByTestId("modal-confirm"));

        await waitFor(() => {
            expect(screen.queryByTestId("modal")).toBeNull();
        });

        expect(screen.getByTestId("username").value).toBe("");
        expect(screen.getByTestId("password").value).toBe("");
        expect(screen.getByTestId("confirm-password").value).toBe("");

        expect(screen.getByTestId("toast")).toHaveTextContent("Registration cancelled");

        act(() => {
            jest.runAllTimers();
        });
        expect(mockNavigate).toHaveBeenCalledWith("/login");
    });
});